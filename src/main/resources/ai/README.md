# Next Market AI Runtime Context

这组文件用于“无向量库”的低成本项目知识注入。

## 推荐目录
复制到 Spring AI 项目：

src/main/resources/ai/
├─ knowledge-index.md
├─ project-overview.md
├─ garden-business.md
├─ finance-business.md
├─ travel-research.md
├─ database-map.md
└─ query-guide.md

## 推荐调用方式

### 第一次调用：Knowledge Router
输入：
- knowledge-index.md
- 用户问题

输出：
- 最多 2 个文档名

示例：
{
  "documents": ["finance-business", "database-map"]
}

### 第二阶段
加载 Router 选择的 Markdown。

然后进入你已有流程：
TableSelection
-> DatabaseTools 取得真实 Schema/DDL
-> QueryPlan
-> SQL / Tool execution
-> Answer

## 为什么不放完整 DDL
完整 DBmeta 很大，而且字段随数据库变化。
运行时知识只保存稳定的“业务含义”和“关系地图”。
真实字段由 DatabaseTools 动态读取，避免：
- 每次浪费大量 token
- 文档和数据库结构不同步
- AI 使用已经删除/修改的字段

## Token 控制建议
- knowledge-index：始终注入，保持极短
- 业务文档：最多选择 2 个
- database-map：仅数据问题加载
- DDL：只获取候选表，不获取全库
- QueryPlan prompt：不要重复整个项目知识
- 最终回答 prompt：只保留执行结果 + 必要业务规则

## 后续升级 RAG
未来加入 VectorStore 时：
- 完整原始文档用于 chunk + embedding
- 当前这些 Markdown 继续保留作为高层业务上下文
- knowledge-index 可保留为 Router 或退化为分类提示
