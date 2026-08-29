package com.next.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

import com.next.ai.service.AiService;
import io.swagger.v3.oas.annotations.Parameter;
import com.next.ai.vo.common.AiResponse;

@RestController
public class AiController {

  private final ChatClient chatClient;
  private final AiService aiService;

  public AiController(ChatClient.Builder builder, AiService aiService) {
    this.chatClient = builder.build();
    this.aiService = aiService;
  }

  @GetMapping("/test")
  public String chat(
      @RequestParam(defaultValue = "你好") String message) {

    return chatClient
        .prompt()
        .user(message)
        .call()
        .content();
  }

  @Operation(summary = "问答", description = "根据自然语言问题，从库中查数据")
  @GetMapping("/data")
  public AiResponse findData(
      @Parameter(description = "用户的自然语言查询", example = "查询中午时段的消费记录") @RequestParam(name = "question", defaultValue = "查询中午时段的消费记录") String question) {
    return aiService.getData(question);
  }
}