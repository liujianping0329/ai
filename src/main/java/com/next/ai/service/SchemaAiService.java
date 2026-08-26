package com.next.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.next.ai.tool.DatabaseTools;

@Service
public class SchemaAiService {
  private final ChatClient chatClient;
  private final TableCatalogService tableCatalogService;
  private final DatabaseTools databaseTools;

  public SchemaAiService(
      ChatClient.Builder builder,
      TableCatalogService tableCatalogService,
      DatabaseTools databaseTools) {
    this.chatClient = builder.build();
    this.tableCatalogService = tableCatalogService;
    this.databaseTools = databaseTools;
  }

  public String getSchema(String question) {

    String systemPrompt = """
        你负责根据用户问题选择数据库表，并读取对应表结构。

            可用业务表：
            %s

            规则：
            - 只能选择上述表
            - 不能创造表名
            - 无法判断时返回 UNKNOWN
            - 确定表以后，必须调用 getTableSchema 工具读取真实表结构
            - 不允许自行猜测字段名
        """.formatted(tableCatalogService.getTables());

    return chatClient.prompt()
        .system(systemPrompt)
        .user(question)
        .tools(databaseTools)
        .call()
        .content();
  }
}
