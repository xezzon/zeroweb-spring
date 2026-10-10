# 每个服务独占自己的数据表

所有服务连接同一个数据库实例（便于本地启动与单机部署），但每张表只属于一个服务，由该服务自己的 Liquibase changelog 管理。跨服务的引用只保留 ID（`owner_id`、`user_id`、`app_id` 等），不建立数据库外键——即使是服务内的关联，也显式声明 `@ForeignKey(ConstraintMode.NO_CONSTRAINT)`。需要其他服务的数据时走 gRPC（`DictService`、`SettingService`、`UserService`），而不是读对方的表。

## Considered Options

- 一个服务一个数据库。被否决：为几个表维护多套连接与多份迁移脚本，运维成本高于收益。
- 允许跨服务关联查询与级联删除。被否决：那等于把这些服务重新耦合成一个单体，失去独立部署与独立演进的能力。

## Consequences

跨服务的一致性由调用方负责，没有数据库层面的兜底。表的所有权必须靠约定维持：改动会波及别的服务的表（如 `zeroweb_i18n_language.parent_id` 指向字典）时需要事先协调。
