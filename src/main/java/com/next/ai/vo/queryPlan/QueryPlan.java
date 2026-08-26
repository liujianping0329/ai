package com.next.ai.vo.queryPlan;

import java.util.List;

public record QueryPlan(
    String table,
    List<String> select,
    List<QueryFilter> filters,
    String orderBy,
    String direction,
    Integer limit) {
}