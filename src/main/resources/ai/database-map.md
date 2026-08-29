# Database Business Map

用途：给 AI 做候选表选择。不是 Schema 替代品。
准确列名、数据类型、nullable、default、constraint、最新外键必须通过 DatabaseTools 获取。

## Core tables

| 表 | 业务职责 | 常见关联 |
|---|---|---|
| f_user | 应用用户 | 多表 userId |
| planet | 数据归属/空间 | 多表 planetId |
| constants | 通用常量/分类 | spend.category |
| garden | Garden/四季园核心记录 | f_user, garden_cate, garden_research |
| garden_cate | Garden 分类层级 | planet |
| garden_labels | Garden 与分类/标签关系 | garden, garden_cate |
| garden_research | 外部研究资料 | garden_research_keyword |
| garden_research_keyword | 研究关键词 | garden_cate |
| garden_remark | Garden 评分/备注 | garden, f_user |
| garden_ai | Garden AI结果 | garden |
| garden_ext | Garden 扩展JSON | garden |
| spend | 支出明细 | constants, f_user, planet, location |
| spend_fix | 固定支出配置 | constants |
| money | 资产/资金快照 | 以日期/快照查询 |
| granary | money 的重复/旧结构 | planet, f_user |
| granary_detail | granary 明细 | granary/user/template |
| location | 地点 | planet; 被 spend 引用 |
| journey | 旅程基础信息 | 当前需按 Schema确认其它关联 |
| news | 新闻/内容/AI问答数据 | 独立内容域 |
| ai_question | AI问题与回答记录 | 独立AI记录 |

## 关键已知外键

### 用户/空间
- f_user.planetId -> planet.id
- constants.planetId -> planet.id
- constants.userId -> f_user.id
- garden.userId -> f_user.id
- garden_cate.planetId -> planet.id
- spend.userId -> f_user.id
- spend.planetId -> planet.id
- location.planetId -> planet.id

### Garden
- garden.cate2 -> garden_cate.id
- garden.researchId -> garden_research.id
- garden_ai.gardenId -> garden.id
- garden_ext.gardenId -> garden.id
- garden_remark.gardenId -> garden.id
- garden_remark.userId -> f_user.id
- garden_labels.gardenId -> garden.id
- garden_labels.cateId -> garden_cate.id
- garden_labels.subCateId -> garden_cate.id
- garden_labels.labelId -> garden_cate.id
- garden_research.keywordId -> garden_research_keyword.id
- garden_research_keyword.cateId -> garden_cate.id

### Finance
- spend.category -> constants.id
- spend.userId -> f_user.id
- spend.planetId -> planet.id
- spend.locationId -> location.id
- spend_fix.spendCate -> constants.id

## 表选择优先级

### 支出
spend -> constants/location/f_user（按问题需要）

### 资产
money
不要默认选择 granary，因为 DB 注释说明 granary 是 money duplicate。

### Garden 主记录
garden
再根据问题增加：
- 分类：garden_cate
- 多标签：garden_labels + garden_cate
- 研究：garden_research
- 备注：garden_remark
- AI：garden_ai

### 外部研究
garden_research
需要关键词时 + garden_research_keyword
需要分类时 + garden_cate

### 旅行
journey
详细子结构必须通过当前 Schema/代码继续确认。

## 选表约束
1. 通常先选 1~3 张候选表。
2. 不要因为表名相似就一起选。
3. 业务地图与数据库实时 Schema 冲突时，以 DatabaseTools 返回为准。
4. SQL 生成之前必须读取实际候选表 DDL/columns。
