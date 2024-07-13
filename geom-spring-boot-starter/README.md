# 公共配置清单

| 变量                          | 描述                                                                          | 默认值                   |
|-----------------------------|-----------------------------------------------------------------------------|-----------------------|
| JDBC_TYPE                   | 数据库类型。<br/>可选值：`postgresql`。                                                | postgresql            |
| SPRING_ENVIRONMENT          | 运行环境。<br/>`dev`：由Hibernate更新数据库字段。Liquibase被禁用。建议本地设置为dev。<br/>`prod`：线上环境。 | prod                  |
| DB_URL                      | 数据库连接地址。格式为`${host}:${port}/${database}`                                    | postgres:5432/geom    |
| DB_USERNAME                 | 数据库连接的用户名。                                                                  |                       |
| DB_PASSWORD                 | 数据库连接的密码。                                                                   |                       |
| GEOM_ID_GENERATOR           | 生成主键的策略。<br/>可选值：`UUID`。                                                    | UUID                  |
| OTEL_SDK_DISABLED           | 是否禁用 OpenTelemetry。仅在 Docker 容器中生效。                                         | true                  |
| OTEL_EXPORTER_OTLP_ENDPOINT | OpenTelemetry Collector 地址                                                  | http://localhost:4317 |

OpenTelemetry 的更多配置项参考[OpenTelemetry SDK Autoconfigure](https://github.com/open-telemetry/opentelemetry-java/tree/main/sdk-extensions/autoconfigure)。
