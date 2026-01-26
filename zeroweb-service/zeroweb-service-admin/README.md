# 系统管理服务

[OpenAPI 规范](https://xezzon.github.io/zeroweb-spring/zeroweb-service/zeroweb-service-admin/openapi.json)

## 安装

### Docker Compose 示例配置

```yaml
# docker-compose.yml
service:
  postgres:
    image: postgres:18  # 关系数据库，强依赖
    environment:
      POSTGRES_DB: zeroweb
      POSTGRES_PASSWORD: postgres@123
  zeroweb-service-admin: # 系统管理服务
    image: ghcr.io/xezzon/zeroweb-service-admin:latest
    name: zeroweb-service-admin
    environment:
      ZEROWEB_ROOT_PASSWORD: zeroweb
      JDBC_TYPE: postgresql
      DB_URL: postgres:5432/zeroweb
      DB_USERNAME: postgres
      DB_PASSWORD: postgres@123
      spring.grpc.client.channels.admin.address: localhost:10002
```

## 配置清单

| 变量                    | 描述                 | 默认值              |
|-----------------------|--------------------|------------------|
| SA_TOKEN_TIMEOUT      | Session 有效时长。单位 秒。 | 2592000（30天）     |
| ZEROWEB_JWT_ISSUER    | JWT签发机构。建议设置为域名。   | xezzon.github.io |
| ZEROWEB_JWT_TIMEOUT   | JWT 有效时长。单位 秒。     | 120              |
| ZEROWEB_ROOT_PASSWORD | root 账号口令。必填。      |                  |

其他配置请查看[公共配置清单](../../zeroweb-spring-boot-starter/README.md)。
