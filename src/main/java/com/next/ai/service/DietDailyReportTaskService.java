package com.next.ai.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.next.ai.vo.diet.DietDailyReportRequest;

@Service
public class DietDailyReportTaskService {

  @Async
  public void generateAsync(DietDailyReportRequest request) {
    System.out.println("收到日报生成请求: " + request.userId() + " / " + request.targetDate());
  }
}
