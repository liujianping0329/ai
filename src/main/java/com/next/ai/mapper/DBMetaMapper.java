package com.next.ai.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.next.ai.vo.metaInfo.ColumnMeta;

@Mapper
public interface DBMetaMapper {

    @Select("""
              SELECT
                a.attname AS "columnName",
                pg_catalog.format_type(a.atttypid, a.atttypmod) AS "dataType",
                CASE
                    WHEN a.attnotnull THEN 'NO'
                    ELSE 'YES'
                END AS "isNullable",
                pg_catalog.col_description(a.attrelid, a.attnum) AS "comment"
            FROM pg_catalog.pg_attribute a
            JOIN pg_catalog.pg_class c
                ON a.attrelid = c.oid
            JOIN pg_catalog.pg_namespace n
                ON c.relnamespace = n.oid
            WHERE n.nspname = 'public'
              AND c.relname = #{tableName}
              AND a.attnum > 0
              AND NOT a.attisdropped
            ORDER BY a.attnum
              """)
    List<ColumnMeta> getMeta(
            @Param("tableName") String tableName);
}