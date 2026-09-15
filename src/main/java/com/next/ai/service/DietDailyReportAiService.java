package com.next.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.next.ai.vo.diet.DietDailyReportRequest;
import com.next.ai.vo.diet.DietDailyReportResult;

@Service
public class DietDailyReportAiService {

  private final ChatClient chatClient;
  private final ObjectMapper objectMapper;

  public DietDailyReportAiService(
      ChatClient.Builder builder) {
    this.chatClient = builder.build();
    this.objectMapper = new ObjectMapper().findAndRegisterModules();
  }

  public DietDailyReportResult generate(DietDailyReportRequest request) {
    try {
      String requestJson = objectMapper.writeValueAsString(request);

      return chatClient
          .prompt()
          .user(user -> user.text("""
              你是家庭饮食日报助手。请依据给出的早、中、晚餐相册成分和每张照片的参与人数，生成简洁、务实的中文日报。

              规则：
              1. 只能基于输入中的食物名称和预估分量判断，不要编造食材、疾病或精确热量。
              2. summary 总结全天饮食结构与主要观察，100字以内。
              3. breakfastAdvice、lunchAdvice、dinnerAdvice 分别给出对应餐次的一条具体建议；没有该餐记录时说明未记录。
              4. futureAttention 给出未来一两天可执行的注意事项，80字以内。
              5. 所有字段使用中文，不要使用 Markdown。

              日报输入：
              """ + requestJson))
          .call()
          .entity(
              DietDailyReportResult.class,
              spec -> spec.validateSchema());
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("日报请求无法序列化", e);
    }
  }
}
