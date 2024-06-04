# 后台管理服务

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
  geom-admin:  # 后台管理服务
    image: ghcr.io/xezzon/geom-service-admin:<version>
    name: geom-admin
    environment:
      JDBC_TYPE: postgresql
      DB_URL: pgsql:5432/postgres
      DB_USERNAME: postgres
      DB_PASSWORD: postgres@123
```

## 配置清单

| 变量 | 描述 | 默认值 |
|----|----|-----|

其他配置请查看[公共配置清单](../../geom-spring-boot-starter/README.md)。

## 接口清单

### 前后端接口

| 接口名称 | 请求方法 | 请求路径           |
|------|------|----------------|
| 用户注册 | POST | /user/register |

### 服务间接口

| 接口名称 | 服务名称                            | 接口标识    |
|------|---------------------------------|---------|
| 新增用户 | io.github.xezzon.geom.user.User | AddUser |

