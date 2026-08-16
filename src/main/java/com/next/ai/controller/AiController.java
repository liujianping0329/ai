package com.next.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiController {

  private final ChatClient chatClient;

  public AiController(ChatClient.Builder builder) {
    this.chatClient = builder.build();
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
}