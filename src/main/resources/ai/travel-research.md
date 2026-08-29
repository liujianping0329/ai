# Travel, Location and Research Knowledge

## journey
职责：保存旅程的基础信息。
已知主要字段：
- id
- title
- startDate
- endDate
- status

适合回答：
- 有哪些旅程
- 某旅程的起止日期
- 旅程状态

如果用户询问旅程内部的详细块、航班、酒店、memo 等，而 journey 当前 Schema 没有对应字段，不应猜测它们一定存储在 journey；需要继续检查代码或其它实际表结构。

## location
职责：可复用地点数据。
信息包括：
- name
- lat / lng
- radius
- spendNames
- planetId
- status

除了旅行场景，location 还被消费 `spend.locationId` 引用，所以它属于跨业务公共表。

## garden_research
职责：保存外部研究/收藏内容。
适用于：
- 抖音/小红书/B站等外部资料
- URL、图片、标题、详情
- 平台/频道信息
- hashtag
- 关键词归类

主要关联：
garden_research.keywordId -> garden_research_keyword.id

## garden_research_keyword
职责：保存研究关键词。
可通过 cateId 关联 garden_cate。

因此“某分类下有哪些研究关键词/研究资料”的典型路径：
garden_cate
-> garden_research_keyword
-> garden_research

## garden 与研究内容
garden.researchId -> garden_research.id

含义：某条 garden 记录可以直接关联一条研究资料。

## news
职责：独立的新闻/内容/AI问答类数据。
已知包含：
- questionDetail
- ansProp
- updated_at
- isPic
- isDetail
- url
- pic
- detailHtml
等。

用户明确询问新闻、新闻详情、新闻问答结果时优先考虑 news；
不要因为 garden_research 也有 url/detail 就把两者混在一起。

## 查询判断
- “收藏/研究资料” -> garden_research
- “研究关键词” -> garden_research_keyword
- “某 Garden 项目关联的资料” -> garden + garden_research
- “地点/经纬度/附近” -> location
- “消费地点” -> spend + location
- “旅程起止时间” -> journey
- “新闻/新闻详情” -> news
