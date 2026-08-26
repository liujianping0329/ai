package com.next.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

import com.next.ai.service.SchemaAiService;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
public class AiController {

  private final ChatClient chatClient;
  private final SchemaAiService aiService;

  public AiController(ChatClient.Builder builder, SchemaAiService aiService) {
    this.chatClient = builder.build();
    this.aiService = aiService;
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

  @Operation(summary = "根据用户问题选择数据库表并查结构", description = "根据自然语言问题，从配置的业务表目录中选择最合适的数据库表并获取其结构")
  @GetMapping("/table")
  public String findTable(
      @Parameter(description = "用户的自然语言查询", example = "查询中午时段的消费记录") @RequestParam(name = "q", defaultValue = "查询中午时段的消费记录") String q) {
    return aiService.getSchema(q);
  }
}