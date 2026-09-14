package com.next.ai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.next.ai.service.DietDailyReportTaskService;
import com.next.ai.vo.diet.DietDailyReportRequest;

@RestController
@RequestMapping("/api/ai/diet")
public class DietDailyReportController {

  private final DietDailyReportTaskService taskService;

  public DietDailyReportController(DietDailyReportTaskService taskService) {
    this.taskService = taskService;
  }

  @PostMapping("/daily-report")
  public ResponseEntity<Void> generate(@RequestBody DietDailyReportRequest request) {
    taskService.generateAsync(request);
    return ResponseEntity.accepted().build();
  }
}
