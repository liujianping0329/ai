package com.next.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.stereotype.Service;

import com.next.ai.tool.DatabaseTools;
import com.next.ai.vo.common.AiResponse;
import com.next.ai.vo.knowledge.KnowledgeSelection;

@Service
public class AiService {
  private final ChatClient chatClient;
  private final TableIntroService tableCatalogService;
  private final DatabaseTools databaseTools;
  private final KnowledgeService knowledgeService;

  public AiService(
      ChatClient.Builder builder,
      TableIntroService tableCatalogService,
      DatabaseTools databaseTools,
      KnowledgeService knowledgeService) {

    this.chatClient = builder.build();
    this.tableCatalogService = tableCatalogService;
    this.databaseTools = databaseTools;
    this.knowledgeService = knowledgeService;
  }

  private KnowledgeSelection selectKnowledge(String question) {

    String prompt = """
        你负责选择回答用户问题所需要的项目知识。

        项目知识目录：

        %s

        规则：
        - 最多选择3个文档
        - 普通业务说明优先选择1个
        - 涉及数据库查询、统计、筛选、排行、SQL、数据位置时，
          必须选择 database-map
        - 如果还需要理解具体业务，再额外选择一个业务文档
        - 不要回答用户问题

        示例：

        用户：这个月食材花了多少钱
        documents:
        - database-map
        - finance-business

        用户：Garden 标签是怎么回事
        documents:
        - garden-business

        用户：Garden记录有哪些表
        documents:
        - database-map
        - garden-business
        """.formatted(knowledgeService.getIndex());

    return chatClient.prompt()
        .system(prompt)
        .user(question)
        .call()
        .entity(KnowledgeSelection.class);
  }

  public AiResponse getData(String question) {
    // 1. 先选择当前问题需要的知识
    KnowledgeSelection selection = selectKnowledge(question);

    // 2. 只加载对应知识
    String knowledge = knowledgeService.load(selection.documents());

    // 3. 原来的查询流程继续
    String systemPrompt = """
        你是本项目的数据库查询助手。

        以下是当前问题相关的项目知识：

        %s

        请基于上述项目知识完成查询。

        执行步骤：
        1. 根据项目知识判断最相关的数据表。
        2. 必须先调用 getTableSchema 获取真实表结构。
        3. 根据真实字段生成 QueryPlan。
        4. 必须调用 queryData 执行查询。
        5. 最终只能根据 queryData 返回的真实数据回答。

        查询规则：
        - 只能使用项目知识中存在的数据表。
        - 字段必须以 getTableSchema 返回结果为准。
        - 不允许创造不存在的表或字段。
        - 只允许 SELECT 查询。
        - limit 最大为 100。

        Filter规则：
        - operator 只能使用：
          EQ, GT, GTE, LT, LTE, LIKE, BETWEEN, TIME_BETWEEN
        - 禁止使用 =、>、>=、<、<=、equals、between 等其它写法。
        - BETWEEN 用于数值、日期、完整时间范围。
        - TIME_BETWEEN 用于一天中的时间段。
        - BETWEEN 和 TIME_BETWEEN 必须同时提供 value1、value2。
        - 没有过滤条件时 filters 可以为空或 null。

        排序规则：
        - direction 只能使用 ASC 或 DESC。
        - ASC = 升序，较早或较小的数据在前。
        - DESC = 降序，较新或较大的数据在前。
        - “最新、最近、最后、倒序、从新到旧”必须使用 DESC。
        - “最早、正序、从旧到新”必须使用 ASC。
        - 禁止使用 forward、backward。

        时间规则：
        - 查询上午、中午、下午、晚上等一天中的时段时，
          使用 timestamp 字段和 TIME_BETWEEN。
        - 例如中午时段：
          operator = TIME_BETWEEN
          value1 = 11:00:00
          value2 = 14:00:00

        最终直接回答用户问题。
        """.formatted(knowledge);

    var chatResponse = chatClient.prompt()
        .system(systemPrompt)
        .user(question)
        .tools(databaseTools)
        .advisors(new SimpleLoggerAdvisor())
        .call()
        .chatResponse();

    return new AiResponse(
        chatResponse.getResult().getOutput().getText(),
        chatResponse.getMetadata().getUsage().getTotalTokens());
  }
}
