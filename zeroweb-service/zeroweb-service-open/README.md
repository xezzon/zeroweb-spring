# 开放平台服务

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
  zeroweb-open:
    image: ghcr.io/xezzon/zeroweb-service-open:<version>
    name: zeroweb-open
```

## 配置清单

| 变量                  | 描述               | 默认值              |
|---------------------|------------------|------------------|
| ZEROWEB_JWT_ISSUER  | JWT签发机构。建议设置为域名。 | xezzon.github.io |
| ZEROWEB_JWT_TIMEOUT | JWT 有效时长。单位 秒。   | 120              |

其他配置请查看[公共配置清单](../../zeroweb-spring-boot-starter/README.md)。

## 接口描述文档

https://xezzon.github.io/zeroweb-spring/zeroweb-service/zeroweb-service-open/smart-doc/index/api.html

## 功能描述

`TODO`
