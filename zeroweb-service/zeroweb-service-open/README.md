# 开放平台服务

## 如何使用

服务构件以 Docker 镜像的形式被发布到 [ghcr](https://ghcr.io)，可以参考如下的 Docker Compose 进行部署。

```yaml
# docker-compose.yml
version: 3
service:
  pgsql:
    image: postgres:16  # 关系数据库，强依赖，目前支持 PostgreSQL
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
