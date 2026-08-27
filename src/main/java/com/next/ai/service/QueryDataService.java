package com.next.ai.service;

import org.springframework.stereotype.Service;

import com.next.ai.mapper.QueryDataMapper;
import com.next.ai.vo.queryPlan.QueryPlan;
import java.util.List;
import java.util.Map;

@Service
public class QueryDataService {
  private final QueryDataMapper queryDataMapper;

  public QueryDataService(
      QueryDataMapper queryDataMapper) {
    this.queryDataMapper = queryDataMapper;
  }

  public List<Map<String, Object>> execute(QueryPlan plan) {
    return queryDataMapper.query(plan);
  }
}
