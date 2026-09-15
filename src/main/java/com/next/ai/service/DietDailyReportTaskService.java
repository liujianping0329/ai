package com.next.ai.service;

import java.time.OffsetDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.next.ai.mapper.DietDailyReportMapper;
import com.next.ai.vo.diet.DietDailyReportRequest;
import com.next.ai.vo.diet.DietDailyReportResult;
import com.next.ai.vo.po.DietDailyReport;

@Service
public class DietDailyReportTaskService {

  private static final Logger log = LoggerFactory.getLogger(DietDailyReportTaskService.class);

  private final DietDailyReportAiService aiService;
  private final DietDailyReportMapper reportMapper;
  private final NextPushService nextPushService;

  public DietDailyReportTaskService(
      DietDailyReportAiService aiService,
      DietDailyReportMapper reportMapper,
      NextPushService nextPushService) {
    this.aiService = aiService;
    this.reportMapper = reportMapper;
    this.nextPushService = nextPushService;
  }

  @Async
  public void generateAsync(DietDailyReportRequest request) {
    try {
      DietDailyReportResult result = aiService.generate(request);
      DietDailyReport report = findReport(request);

      report.setStatus("completed");
      report.setSummary(result.summary());
      report.setBreakfastAdvice(result.breakfastAdvice());
      report.setLunchAdvice(result.lunchAdvice());
      report.setDinnerAdvice(result.dinnerAdvice());
      report.setFutureAttention(result.futureAttention());
      report.setErrorMessage(null);
      report.setCompletedAt(OffsetDateTime.now());
      reportMapper.updateById(report);
      log.info("日报生成完成: {}", result);
      pushReport(request, result, report);
    } catch (Exception e) {
      markFailed(request, e);
    }
  }

  private DietDailyReport findReport(DietDailyReportRequest request) {
    DietDailyReport report = reportMapper.selectOne(
        Wrappers.<DietDailyReport>lambdaQuery()
            .eq(DietDailyReport::getUserId, request.userId())
            .eq(DietDailyReport::getTargetDate, request.targetDate()));

    if (report == null) {
      throw new IllegalArgumentException("日报记录不存在");
    }

    return report;
  }

  private void markFailed(DietDailyReportRequest request, Exception error) {
    try {
      DietDailyReport report = findReport(request);
      report.setStatus("report_failed");
      report.setErrorMessage(error.getMessage());
      reportMapper.updateById(report);
    } catch (Exception updateError) {
      System.err.println("日报失败状态更新失败: " + updateError.getMessage());
    }
  }

  private void pushReport(
      DietDailyReportRequest request,
      DietDailyReportResult result,
      DietDailyReport report) {
    try {
      nextPushService.pushDailyReport(request, result);
      report.setReportPushAt(OffsetDateTime.now());
      reportMapper.updateById(report);
    } catch (Exception e) {
      log.error("日报推送失败: {}", e.getMessage());
    }
  }
}
