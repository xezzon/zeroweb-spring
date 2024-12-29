# ZeroWeb

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=xezzon_zeroweb-spring&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=xezzon_zeroweb-spring)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=xezzon_zeroweb-spring&metric=coverage)](https://sonarcloud.io/summary/new_code?id=xezzon_zeroweb-spring)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=xezzon_zeroweb-spring&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=xezzon_zeroweb-spring)

ZeroWeb 是一组 BaaS(Backend as a Service)，可以低成本地实现认证授权、后台管理、开放平台等功能。提供 gRPC 接口 SDK（后端集成）与 HTTP 接口 SDK（前端集成）。

## 功能特性

- [后台管理服务](zeroweb-service/zeroweb-service-admin/README.md)
  - 认证
  - 单点登录
  - 字典管理
- [服务间接口SDK](zeroweb-proto/README.md)
  - 第三方应用管理
  - 对外接口的管理、订阅、调用

## 使用方式

### 独立部署

本项目优先支持此方式。

本项目的每一个服务都会以镜像的形式发布到 ghcr。开发与部署时选择所需要的服务部署，然后通过集成业务系统语言对应的SDK，调用ZeroWeb服务进行交互。其中 zeroweb-service-admin 服务是必须部署的，因为其被其他所有服务所依赖。除非特殊说明，其他服务之间不会有强依赖关系。

### 二次开发

即在本项目的源代码基础上进行修改。该方式违背了这个项目的初衷，所以不会获得任何支持，不推荐使用该方式。

本项目遵循 LGPL 3.0 开源协议，对源代码的修改需要遵循相同的协议进行开源。

## 应用架构

```mermaid
C4Component
  title 部署架构（参考）
  Node(browser, "浏览器", "Google Chrome, Mozilla Firefox, Apple Safari or Microsoft Edge") {
    Container(spa, "前端应用", "React")
  }
  Node(cf, "DNS", "cloudflare") {
    Container(CDN, "CDN")
    Container(gw, "公网网关")
  }
  Node(app, "后端应用") {
    Container(api, "API网关", "river", "后端应用唯一对外暴露的节点")
    Container(zeroweb-service-admin, "后台管理服务", "Spring Boot")
    Container(zeroweb-service-openapi, "开放平台服务", "Spring Boot")
    Container(service-a, "业务应用1")
    
    Rel_D(api, zeroweb-service-admin, "admin.domain.com", "json/HTTP")
    Rel_D(api, zeroweb-service-openapi, "openapi.domain.com", "json/HTTP")
    Rel_D(api, service-a, "a.domain.com", "json/HTTP")
          
    Rel(zeroweb-service-openapi, zeroweb-service-admin, "RPC调用", "protobuf/gRPC")
    Rel(service-a, zeroweb-service-admin, "RPC调用", "protobuf/gRPC")
  }
  Node(dep, "中间件") {
    Container(db, "关系型数据库", "PostgreSQL")
    Container(kv, "键值数据库", "Redis")
  }
  Rel_D(spa, CDN, "www.domain.com", "html/HTTPs")
  Rel_D(spa, gw, "*.domain.com", "json/HTTPs")
  Rel_D(gw, api, "*.domain.com", "json/HTTPs")
  Rel_D(zeroweb-service-admin, db, "持久化数据", "JDBC")
  Rel_D(service-a, db, "持久化数据", "JDBC")
  Rel_D(zeroweb-service-admin, kv, "数据共享", "Lettuce")
```

## [开发者手册](./CONTRIBUTING.md)

## [技术栈](https://xezzon.github.io/zeroweb-spring/dependencies.html)

## [License](https://xezzon.github.io/zeroweb-spring/licenses.html)

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fxezzon%2Fzeroweb-spring.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Fxezzon%2Fzeroweb-spring?ref=badge_large)
