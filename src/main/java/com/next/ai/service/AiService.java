package com.next.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.next.ai.tool.DatabaseTools;
import com.next.ai.vo.queryPlan.QueryPlan;

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

  public QueryPlan getData(String question) {

    String systemPrompt = """
        你负责根据用户问题生成数据库查询计划。

        可用业务表：
        %s

        规则：
        - 只能使用给出的业务表
        - 必须先调用 getTableSchema 获取真实字段
        - 不允许创造不存在的字段
        - 只允许查询，不允许 INSERT、UPDATE、DELETE
        - limit 最大为 100
        - operator 只能使用：
          EQ, GT, GTE, LT, LTE, BETWEEN, TIME_BETWEEN, LIKE
        """.formatted(tableCatalogService.getTables());

    return chatClient.prompt()
        .system(systemPrompt)
        .user(question)
        .tools(databaseTools)
        .call()
        .entity(QueryPlan.class);
  }
}
