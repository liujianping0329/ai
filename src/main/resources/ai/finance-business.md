# Finance Business Knowledge

## 支出明细：spend
职责：记录具体消费/支出，是日常消费查询的第一候选表。

主要业务字段：
- date：消费日期
- category：消费分类
- title：标题
- amount：金额
- cashType：支付/币种相关类型
- cnyToJpy / twdToJpy / usdToJpy：当次记录涉及的汇率信息
- userId：用户
- planetId：业务空间
- isFix：是否固定支出
- locationId：地点

关联：
- spend.category -> constants.id
- spend.userId -> f_user.id
- spend.planetId -> planet.id
- spend.locationId -> location.id

## 分类/常量：constants
职责：保存通用枚举、分类和层级配置。
常见字段：
- category
- label
- value
- children
- sort
- planetId
- userId

查询支出分类名称时，通常：
spend.category -> constants.id

不要假设 constants 只有消费分类，它是通用常量表。需要通过 category/value 等条件限定类型。

## 固定费用：spend_fix
职责：保存固定支出模板/配置。
已知关联：
- spend_fix.spendCate -> constants.id

用户问“固定费用配置/每月固定支出来源”时优先考虑 spend_fix；
用户问“某个月实际发生了哪些固定费用”时应优先看 spend.isFix 及当月 spend 数据，而不是只查模板。

## 资产/资金快照：money
职责：保存某时间点的资金/资产汇总。
主要业务信息：
- cnyToJpy
- twdToJpy
- usdToJpy
- total
- detail
- date
- newSysExt

money 更接近“某日资产快照”，与逐笔消费 `spend` 不同。

## granary
数据库注释：`This is a duplicate of money`。
因此：
- 普通资产问题优先使用 money
- 只有用户/代码明确使用 granary 时才选择 granary

`granary_detail` 是 granary 体系的明细，并有关联 user/template/granary。

## 地点：location
职责：保存可复用地理位置/消费地点信息。
主要信息：
- name
- lat
- lng
- planetId
- radius
- spendNames
- status

关系：
- spend.locationId -> location.id

## 常见问题映射

### “这个月花了多少钱？”
主表：spend
条件：date 在目标月份
统计：SUM(amount)

### “本月各分类花费”
spend
-> constants
GROUP BY 分类

### “固定支出多少？”
若问实际发生：
spend WHERE isFix = true
若问固定支出配置：
spend_fix

### “某地点花了多少钱？”
spend
-> location
按 location 或 location.name 汇总

### “某人的支出”
spend.userId -> f_user.id
需要用户范围时必须带 userId 条件

### “当前/某日资产”
money
按 date 选择对应快照；不要用 spend SUM 代替资产余额。

## AI注意事项
- 金额统计前确认币种/amount 的业务含义；不要擅自把汇率字段二次换算。
- 分类名称通常需要 join constants。
- 时间范围必须明确到 date。
- 用户/空间隔离优先检查 userId、planetId。
