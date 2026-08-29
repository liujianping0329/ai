# Next Market AI Knowledge Index

用途：仅用于第一阶段知识路由。根据用户问题选择 0~2 个知识文件，不直接回答业务问题。

## project-overview
文件：project-overview.md
包含：系统定位、主要模块、模块边界、数据隔离概念。
适用问题：系统做什么、某功能属于哪个模块、多个模块之间如何配合。

## garden-business
文件：garden-business.md
包含：四季园/花园类功能、记录/分类/标签/研究资料/备注/AI结果之间的关系。
适用问题：春夏秋冬项目、garden、分类、标签、收藏研究资料、评分备注、AI分析。

## finance-business
文件：finance-business.md
包含：支出、固定支出、资产/资金快照、汇率、地点与用户归属。
适用问题：消费记录、月度统计、分类统计、固定费用、资产、汇率、地点消费。

## travel-research
文件：travel-research.md
包含：旅程、地点、研究资料、新闻/外部内容等辅助信息。
适用问题：行程、旅行记录、地点、外部资料收集、研究内容。

## database-map
文件：database-map.md

职责：
项目数据库的业务地图，是选择数据库表的主要依据。

包含：
- 表名
- 表的业务职责
- 核心关联关系
- 常见查询路径
- 表选择规则

适用于：
- 查询数据
- 生成 SQL
- 判断应该使用哪些表
- 判断表之间如何关联

规则：
只要用户问题需要读取数据库数据，必须加载本文件。

注意：
本文件不代表实时 Schema。
字段、类型、约束必须通过 DatabaseTools 获取。

## query-guide
文件：query-guide.md
包含：自然语言问题 -> 业务对象 -> 候选表的快速映射，以及 SQL 查询原则。
适用问题：数据查询、统计、排行、筛选、跨表查询、SQL 规划。

## Routing Rules

1. 普通业务说明优先选择 1 个文件。

2. 涉及数据库查询、统计、筛选、排行、SQL 或数据位置时，
   优先选择 `database-map`，并根据问题加载相关业务文件。

3. 一般选择 1~3 个文件。
   如果问题明显涉及多个业务域，可以选择更多，但应尽量减少无关上下文。

4. 不要因为出现“表”“SQL”“数据”等关键词就加载所有业务文档。

5. 已能通过 `database-map` 明确判断表和业务含义时，
   可以只选择 `database-map`。

6. 详细字段、DDL、nullable、default、constraint 等信息
   不从知识文件获取，应调用 DatabaseTools。
