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

        执行规则：

        1. 根据项目知识判断最可能相关的数据表。
        2. 在生成查询计划之前，必须调用 getTableSchema 获取实际表结构。
        3. 项目知识只用于理解业务和选择表。
        4. 字段、类型、约束等信息必须以 getTableSchema 返回结果为准。
        5. 不允许自行创造不存在的字段或数据表。
        6. 必须调用 queryData 获取真实数据。
        7. 最终只能根据 queryData 返回的数据回答用户。
        8. 只允许查询，不允许 INSERT、UPDATE、DELETE。

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
