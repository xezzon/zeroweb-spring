# 开放平台服务

[OpenAPI 规范](https://xezzon.github.io/zeroweb-spring/zeroweb-service/zeroweb-service-open/openapi.json)

## 安装

### Docker Compose 示例配置

```yaml
# docker-compose.yml
service:
  postgres:
    image: postgres:18
    environment:
      POSTGRES_DB: zeroweb
      POSTGRES_PASSWORD: postgres@123
  zeroweb-service-open:
    image: ghcr.io/xezzon/zeroweb-service-open:latest
    name: zeroweb-service-open
    environment:
      DB_USERNAME: postgres
      DB_PASSWORD: postgres@123
```

## 配置清单

| 变量                  | 描述               | 默认值              |
|---------------------|------------------|------------------|
| ZEROWEB_JWT_ISSUER  | JWT签发机构。建议设置为域名。 | xezzon.github.io |
| ZEROWEB_JWT_TIMEOUT | JWT 有效时长。单位 秒。   | 120              |

其他配置请查看[公共配置清单](../../zeroweb-spring-boot-starter/README.md)。

## 功能描述

`TODO`
