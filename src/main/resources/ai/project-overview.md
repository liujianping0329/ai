# Next Market Project Overview

## 系统定位
Next Market 是一个基于 Next.js + Supabase/PostgreSQL 的个人信息管理应用。系统把日常信息按业务域组织，包括花园/项目记录、消费与资产、研究资料、地点和旅程等。

AI 后端不应把所有表视为同一业务。回答问题时先判断业务域，再选择候选表。

## 核心业务域

### 1. Garden / 四季园
核心记录表为 `garden`。
围绕记录扩展分类、标签、研究资料、备注评分和 AI 结果。
主要表：
- garden
- garden_cate
- garden_labels
- garden_research
- garden_research_keyword
- garden_remark
- garden_ai
- garden_ext

### 2. Finance / 消费与资产
消费明细以 `spend` 为核心；分类来自 `constants`。
资产/资金快照主要使用 `money`；`granary` 是 money 的重复/旧结构，应避免无理由优先选择。
主要表：
- spend
- spend_fix
- money
- money_memo
- constants
- location

### 3. Travel / Location
旅程使用 `journey`；地点基础数据使用 `location`。
地点也可能被消费记录通过 `spend.locationId` 引用。

### 4. Research / External Content
外部研究资料主要保存在 `garden_research`。
内容可关联搜索关键词 `garden_research_keyword`，并可被 `garden.researchId` 引用。
`news` 保存新闻/问答/详情类内容，属于独立辅助内容域。

### 5. User / Scope
`f_user` 表示应用用户。
`planet` 是多处业务数据的归属/空间维度。
很多业务表通过 `userId` 或 `planetId` 进行数据隔离。

## AI 查询原则
1. 先理解业务含义，再选表。
2. 文档只用于决定“查什么”；真实 Schema 使用 DatabaseTools 动态读取。
3. 不确定时宁可扩大候选表 1~2 张，不要直接扫描全部表。
4. 查询用户数据时注意 `userId` / `planetId` 限定。
5. `granary` 数据库注释明确标为 money 的 duplicate；除非问题明确涉及 granary，否则资产查询优先考虑 `money`。
