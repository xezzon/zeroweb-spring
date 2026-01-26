# 研发平台服务

[OpenAPI 规范](https://xezzon.github.io/zeroweb-spring/zeroweb-service/zeroweb-service-dev/openapi.json)

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
  zeroweb-open:
    image: ghcr.io/xezzon/zeroweb-service-dev:latest
    name: zeroweb-service-dev
    environment:
      DB_USERNAME: postgres
      DB_PASSWORD: postgres@123
```
