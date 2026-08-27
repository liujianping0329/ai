package com.next.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.stereotype.Service;

import com.next.ai.tool.DatabaseTools;

@Service
public class AiService {
  private final ChatClient chatClient;
  private final TableIntroService tableCatalogService;
  private final DatabaseTools databaseTools;

  public AiService(
      ChatClient.Builder builder,
      TableIntroService tableCatalogService,
      DatabaseTools databaseTools) {
    this.chatClient = builder.build();
    this.tableCatalogService = tableCatalogService;
    this.databaseTools = databaseTools;
  }

  public String getData(String question) {

    String systemPrompt = """
        你是数据库查询助手。

        可用业务表：
        %s

        你需要完整完成用户的数据查询请求。

        执行步骤：
        1. 根据用户问题选择最合适的业务表。
        2. 必须先调用 getTableSchema 获取真实字段结构。
        3. 根据真实字段生成查询计划。
        4. 必须调用 queryData 工具执行查询。
        5. 只能根据 queryData 返回的真实数据回答用户。
        6. 不允许自行猜测数据库中不存在的数据。

        查询规则：
        - 只能使用给出的业务表
        - 不允许创造不存在的字段
        - 只允许查询，不允许 INSERT、UPDATE、DELETE
        - limit 最大为 100
        - operator 只能使用：
          EQ, GT, GTE, LT, LTE, BETWEEN, TIME_BETWEEN, LIKE

        最终直接回答用户的查询结果，不要只返回 QueryPlan。
            """.formatted(tableCatalogService.getTables());

    return chatClient.prompt()
        .system(systemPrompt)
        .user(question)
        .tools(databaseTools)
        .advisors(new SimpleLoggerAdvisor())
        .call()
        .content();
  }
}
