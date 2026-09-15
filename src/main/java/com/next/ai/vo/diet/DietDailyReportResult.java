package com.next.ai.vo.diet;

public record DietDailyReportResult(
    String summary,
    String breakfastAdvice,
    String lunchAdvice,
    String dinnerAdvice,
    String futureAttention) {
}
