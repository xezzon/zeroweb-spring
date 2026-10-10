# 字典在代码中声明

字典不通过界面或 SQL 手工维护，而是在代码中以实现了 `IDict` 的枚举声明：`tag` 为字典目，`code` 为字典键，`label` 为字典值。应用用 `@EnableDictScan` 指定扫描包，启动时扫描这些枚举并导入数据库；导入是「已存在则跳过」，不覆盖既有数据。系统管理服务本地落库并持有 `zeroweb_dict` 表（`DictDbHandler`），其他服务经 `DictService.ImportDict` 提交（`DictRpcHandler`）。

这样字典随代码一起评审、发布和回滚，各环境不需要各自维护一份字典数据，也避免了枚举与数据库漂移。
