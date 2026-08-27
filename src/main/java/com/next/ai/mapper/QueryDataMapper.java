package com.next.ai.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.next.ai.vo.queryPlan.QueryPlan;
import java.util.List;
import java.util.Map;

@Mapper
public interface QueryDataMapper {
  List<Map<String, Object>> query(QueryPlan plan);
}
