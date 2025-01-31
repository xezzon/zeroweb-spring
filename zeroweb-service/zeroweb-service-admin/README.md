# 后台管理服务

## 安装

### Docker Compose 示例配置

```yaml
# docker-compose.yml
version: 3
service:
  pgsql:
    image: postgres:16  # 关系数据库，强依赖
    name: pgsql
    environment:
      POSTGRES_PASSWORD: postgres@123
  zeroweb-admin: # 后台管理服务
    image: ghcr.io/xezzon/zeroweb-service-admin:<version>
    name: zeroweb-admin
    environment:
      JDBC_TYPE: postgresql
      DB_URL: pgsql:5432/postgres
      DB_USERNAME: postgres
      DB_PASSWORD: postgres@123
```

## 配置清单

| 变量                  | 描述                 | 默认值              |
|---------------------|--------------------|------------------|
| SA_TOKEN_TIMEOUT    | Session 有效时长。单位 秒。 | 2592000（30天）     |
| ZEROWEB_JWT_ISSUER  | JWT签发机构。建议设置为域名。   | xezzon.github.io |
| ZEROWEB_JWT_TIMEOUT | JWT 有效时长。单位 秒。     | 120              |

其他配置请查看[公共配置清单](../../zeroweb-spring-boot-starter/README.md)。

## 接口描述文档

https://xezzon.github.io/zeroweb-spring/zeroweb-service/zeroweb-service-admin/smart-doc/index/api.html
