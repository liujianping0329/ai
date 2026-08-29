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
                pg_catalog.col_description(a.attrelid, a.attnum) AS "comment",

                fk.ref_table AS "referencedTable",
                fk.ref_column AS "referencedColumn"

            FROM pg_catalog.pg_attribute a

            JOIN pg_catalog.pg_class c
                ON a.attrelid = c.oid

            JOIN pg_catalog.pg_namespace n
                ON c.relnamespace = n.oid

            LEFT JOIN (
                SELECT
                    con.conrelid,
                    unnest(con.conkey) AS column_num,
                    ref.relname AS ref_table,
                    ref_att.attname AS ref_column
                FROM pg_catalog.pg_constraint con
                JOIN pg_catalog.pg_class ref
                    ON ref.oid = con.confrelid
                JOIN LATERAL unnest(con.confkey)
                    WITH ORDINALITY AS fk(attnum, ord)
                    ON TRUE
                JOIN pg_catalog.pg_attribute ref_att
                    ON ref_att.attrelid = con.confrelid
                   AND ref_att.attnum = fk.attnum
                WHERE con.contype = 'f'
            ) fk
                ON fk.conrelid = a.attrelid
               AND fk.column_num = a.attnum

            WHERE n.nspname = 'public'
              AND c.relname = #{tableName}
              AND a.attnum > 0
              AND NOT a.attisdropped

            ORDER BY a.attnum
                          """)
    List<ColumnMeta> getMeta(
            @Param("tableName") String tableName);
}