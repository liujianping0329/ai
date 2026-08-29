# Garden / 四季园 Business Knowledge

## 核心概念
四季园可以理解为围绕 `garden` 主记录构建的一组项目/生活记录功能。不同页面可以对 garden 记录采用不同分类、状态和展示方式，但数据中心仍是 `garden`。

## garden
职责：保存一条核心记录。
常见业务信息：
- 日期：date
- 标题：title
- 正文：content
- 图片：pics
- 地点：location
- 状态：status
- 备注：remark
- 评分/点数：point
- 分类/类型：category
- 主题：topic
- 排序：sort
- 父记录：parent
- 用户归属：userId
- 二级分类：cate2
- 研究资料关联：researchId

`category` 的含义会随 garden 子功能变化。数据库注释指出：
- greengrass 场景中可作为分类
- soyBean 场景中用于区分 folder/item/空值等层级性质

因此不要仅凭 `category` 字段名推断固定业务枚举。

## 分类：garden_cate
职责：保存 Garden 业务中的分类/子分类/标签基础项。
主要关系：
- garden.cate2 -> garden_cate.id
- garden_cate.parentId 表示分类层级
- garden_cate.planetId -> planet.id

## 标签关系：garden_labels
职责：把 garden 记录与分类/子分类/标签连接。
关系：
- garden_labels.gardenId -> garden.id
- cateId -> garden_cate.id
- subCateId -> garden_cate.id
- labelId -> garden_cate.id

需要查询“某记录有哪些标签”时：
garden -> garden_labels -> garden_cate

## 研究资料：garden_research
职责：保存从外部平台收集的研究内容。
重要信息包括：
- channel
- platformId
- title
- detail
- url
- image
- hashtags
- platform
- keywordId

关联：
- garden.researchId -> garden_research.id
- garden_research.keywordId -> garden_research_keyword.id

## 研究关键词：garden_research_keyword
职责：定义研究抓取/搜索时使用的关键词，并可与 Garden 分类关联。
关系：
- cateId -> garden_cate.id

## 备注与评价：garden_remark
职责：用户对 garden 记录进行评分、备注及图片补充。
关系：
- gardenId -> garden.id
- userId -> f_user.id

## AI结果：garden_ai
职责：保存针对 garden 记录生成的 AI 输出。
关系：
- gardenId -> garden.id
内容：
- ans：文本答案
- ansJSON：结构化答案
- cost：AI调用成本信息

## 扩展信息：garden_ext
职责：保存不适合直接放进 garden 主表的扩展 JSON 信息。
关系：
- gardenId -> garden.id
- refInfo：扩展引用数据

## 常见查询路线

### 查记录本身
garden

### 查记录 + 分类
garden
-> garden_cate（通过 garden.cate2）

### 查记录 + 多级标签
garden
-> garden_labels
-> garden_cate

### 查记录关联的外部研究
garden
-> garden_research

### 查研究资料对应关键词
garden_research
-> garden_research_keyword
-> garden_cate（必要时）

### 查某记录的评价
garden
-> garden_remark
-> f_user（需要用户信息时）

### 查某记录AI结果
garden
-> garden_ai

## AI注意事项
- Garden 业务存在多种前端用途，不能把 status/category 机械解释成全局统一枚举。
- 需要判断具体状态含义时，应结合用户问题及相关代码/常量，而非只读字段名。
- 查询明细前先确认是否需要 userId/planetId 范围。
