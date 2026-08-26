package com.next.ai.vo.queryPlan;

public record QueryFilter(
    String field,
    String operator,
    String value1,
    String value2) {
}