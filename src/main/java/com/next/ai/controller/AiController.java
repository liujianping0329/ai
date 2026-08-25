package com.next.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.next.ai.vo.TableSelection;

import io.swagger.v3.oas.annotations.Operation;

import com.next.ai.service.TableSelectionService;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
public class AiController {

  private final ChatClient chatClient;
  private final TableSelectionService tableSelectionService;

  public AiController(ChatClient.Builder builder, TableSelectionService tableSelectionService) {
    this.chatClient = builder.build();
    this.tableSelectionService = tableSelectionService;
  }

  @GetMapping("/api/ai")
  public String chat(
      @RequestParam(defaultValue = "你好") String message) {

    return chatClient
        .prompt()
        .user(message)
        .call()
        .content();
  }

  @Operation(summary = "根据用户问题选择数据库表", description = "根据自然语言问题，从配置的业务表目录中选择最合适的数据库表")
  @GetMapping("/table")
  public TableSelection findTable(
      @Parameter(description = "用户的自然语言查询", example = "查询中午时段的消费记录") @RequestParam(name = "q", defaultValue = "查询中午时段的消费记录") String q) {
    return tableSelectionService.selectTable(q);
  }
}