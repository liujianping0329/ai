package com.next.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import com.next.ai.vo.TableSelection;

@Service
public class TableSelectionService {
  private final ChatClient chatClient;
  private final TableCatalogService tableCatalogService;

  public TableSelectionService(
      ChatClient.Builder builder,
      TableCatalogService tableCatalogService) {
    this.chatClient = builder.build();
    this.tableCatalogService = tableCatalogService;
  }

  public TableSelection selectTable(String question) {

    String systemPrompt = """
        你负责根据用户问题选择数据库表。

        可用业务表：
        %s

        规则：
        - 只能选择上述表
        - 不能创造表名
        - 无法判断时返回 UNKNOWN
        """.formatted(tableCatalogService.getTables());

    return chatClient.prompt()
        .system(systemPrompt)
        .user(question)
        .call()
        .entity(TableSelection.class);
  }
}
