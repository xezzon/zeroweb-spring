# 公共配置清单

| 变量                          | 描述                                                                          | 默认值                   |
|-----------------------------|-----------------------------------------------------------------------------|-----------------------|
| JDBC_TYPE                   | 数据库类型。<br/>可选值：`postgresql`。                                                | postgresql            |
| SPRING_ENVIRONMENT          | 运行环境。<br/>`dev`：由Hibernate更新数据库字段。Liquibase被禁用。建议本地设置为dev。<br/>`prod`：线上环境。 | prod                  |
| DB_URL                      | 数据库连接地址。格式为`${host}:${port}/${database}`                                    | postgres:5432/zeroweb |
| DB_USERNAME                 | 数据库连接的用户名。                                                                  |                       |
| DB_PASSWORD                 | 数据库连接的密码。                                                                   |                       |
| CACHE_TYPE                  | KV数据库类型。若以集群模式部署，则此项必填。<br/>可选值：`in-memory`, `redis`。                       | in-memory             |
| REDIS_URL                   | REDIS 连接信息。如果以集群模式部署该服务，则此项必填。格式为：`user:password@host:port`或`host:port`     |                       |
| REDIS_DATABASE              | REDIS 使用的库号。                                                                | 0                     |
| ZEROWEB_ID_GENERATOR        | 生成主键的策略。<br/>可选值：`UUID`。                                                    | UUID                  |
| OTEL_SDK_DISABLED           | 是否禁用 OpenTelemetry。                                                         | true                  |
| OTEL_EXPORTER_OTLP_ENDPOINT | OpenTelemetry Collector 地址                                                  | http://localhost:4317 |

OpenTelemetry 的更多配置项参考[OpenTelemetry SDK Autoconfigure](https://opentelemetry.io/docs/languages/java/configuration/)。
