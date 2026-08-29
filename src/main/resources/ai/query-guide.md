# Natural Language Query Guide

## 目标
把用户自然语言转换为：
1. 业务域
2. 候选表
3. 必要 JOIN
4. 时间/用户/空间过滤
然后再让 DatabaseTools 提供真实 Schema。

## 快速映射

| 用户意图 | 主表 | 可能附加表 |
|---|---|---|
| 支出/消费/花费 | spend | constants, location, f_user |
| 分类消费统计 | spend | constants |
| 固定支出实际发生 | spend | constants |
| 固定费用配置 | spend_fix | constants |
| 资产/余额/资金快照 | money | - |
| Garden记录 | garden | garden_cate |
| Garden标签 | garden_labels | garden, garden_cate |
| Garden备注/评分 | garden_remark | garden, f_user |
| Garden AI结果 | garden_ai | garden |
| Garden关联研究资料 | garden | garden_research |
| 外部收藏/研究 | garden_research | garden_research_keyword |
| 研究关键词 | garden_research_keyword | garden_cate |
| 地点 | location | planet |
| 地点消费 | spend | location |
| 旅程 | journey | 按实际Schema |
| 新闻/新闻详情 | news | - |
| AI问题历史 | ai_question | - |

## 查询步骤

### Step 1：提取条件
从问题中识别：
- 时间：今天/本月/某年某月/区间
- 用户
- planet/空间
- 分类
- 状态
- 地点
- 排序/Top N
- 聚合方式：sum/count/avg/max/min

### Step 2：选主表
先找“事实发生在哪里”：
- 消费发生在 spend
- Garden记录发生在 garden
- 资产快照在 money
- 研究资料在 garden_research

### Step 3：只为展示/过滤所需信息 JOIN
例如：
“本月各消费分类金额”
需要分类名称：
spend -> constants

“本月总消费”
不需要分类名称：
只查 spend

避免无意义 JOIN。

### Step 4：获取真实 Schema
读取候选表真实列和外键。
不要直接根据本知识文件生成最终 SQL。

### Step 5：生成 SQL
优先：
- 明确列名，避免 SELECT *
- 参数化值
- 限制用户/planet范围
- 聚合查询只返回必要字段
- 大列表使用 LIMIT

## 例子

### Q：2026年6月食材花了多少？
业务：Finance
候选：spend + constants
条件：
- spend.date 在 2026-06
- constants 对应“食材”
聚合：SUM(spend.amount)

### Q：最近10条 Garden 记录
业务：Garden
候选：garden
条件：按日期/创建时间倒序
限制：10
无需加载 labels/research 等表。

### Q：某 Garden 记录有哪些标签？
候选：
garden_labels + garden_cate
必要时 garden 用于确认记录范围。

### Q：我收藏的小红书研究资料
候选：garden_research
过滤：platform/channel 等实际字段值
不需要 garden，除非用户问“已经绑定到某 Garden 项目的资料”。

### Q：某地点本月消费
候选：spend + location
条件：日期 + location
聚合：SUM(amount)

## 防止幻觉
- 不知道枚举值：先查实际数据/常量，不编造。
- 不知道字段：调用 DatabaseTools。
- 不确定业务表：返回少量候选表再读取 Schema。
- 文档中没有说明的前端临时结构，不等于数据库一定存在对应字段。
