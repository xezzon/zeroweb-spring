# ZeroWeb

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=xezzon_zeroweb-spring&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=xezzon_zeroweb-spring)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=xezzon_zeroweb-spring&metric=coverage)](https://sonarcloud.io/summary/new_code?id=xezzon_zeroweb-spring)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=xezzon_zeroweb-spring&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=xezzon_zeroweb-spring)


ZeroWeb 是一组 BaaS(Backend as a Service)，可以低成本地实现认证授权、系统管理、开放平台等功能。提供 gRPC 接口 SDK（后端集成）与 HTTP 接口 SDK（前端集成）。

## 功能特性

- [系统管理服务](zeroweb-service/zeroweb-service-admin/README.md)
  - 认证
  - 单点登录
  - RBAC模型授权
  - 字典管理
- [研发平台服务](zeroweb-service/zeroweb-service-open/README.md)
  - 国际化管理
- [系统管理服务](zeroweb-service/zeroweb-service-open/README.md)
  - 第三方应用管理
  - 第三方应用调用本系统 HTTP 接口
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

![应用架构](docs/excalidraw/component.svg)

![部署架构](docs/excalidraw/deploy.svg)

## 与同类产品的比较

### 功能特性

|           | ZeroWeb  | [RuoYi-Vue-Pro](https://gitee.com/zhijiantianya/ruoyi-vue-pro) |
|-----------|----------|----------------------------------------------------------------|
| 认证授权      | √        | √                                                              |
| 用户管理      | 未来支持     | √                                                              |
| 部门管理      | 暂无计划     | √                                                              |
| 岗位管理      | 暂无计划     | √                                                              |
| 租户管理      | 通过运维手段解决 | √                                                              |
| 字典管理      | √        | √                                                              |
| 参数管理      | √        | √                                                              |
| 消息通知      | 未来支持     | √                                                              |
| 附件管理      | √        | ×                                                              |
| 第三方应用集成平台 | √        | ×                                                              |
| WebHook   | 未来支持     | ×                                                              |
| 国际化管理     | √        | √                                                              |
| 定时任务      | ×        | √                                                              |
| 代码生成      | ×        | ×                                                              |
| 流程平台      | 未来支持     | √                                                              |
| 操作日志      | 通过运维手段解决 | √                                                              |
| 系统监控      | 通过运维手段解决 | √                                                              |

### 运维方式

|             | ZeroWeb                                          | [RuoYi-Vue-Pro](https://gitee.com/zhijiantianya/ruoyi-vue-pro)     |
|-------------|--------------------------------------------------|--------------------------------------------------------------------|
| 首选集成方式      | 通过 Docker 镜像部署容器。可以与任意技术栈进行集成。                   | 拉代码进行二次开发。技术栈基本取决于 RuoYi。                                          |
| 交互方式        | 前端通过 HTTP 或者封装的 JS SDK 进行交互。后端通过 gRPC 进行交互。      | 前端通过 HTTP 进行交互。未封装 SDK，需要拉对应的前端代码。                                 |
| 应用配置        | 推荐修改的配置通过环境变量暴露出来（可以很好地兼容 Docker 与 K8s，也兼容配置中心）。 | 所有配置都需要改配置文件（虽然也可以通过环境变量，但是支持并不良好）。不容易做到不改配置的情况下分发多套软件（即一套代码到处运行）。 |
| DDL管理       | 使用 Liquibase 自动化迁移。应用启动时即执行 DDL。                 | 提供 DDL 语句，需要使用者手动执行。                                               |
| 遥测（链路追踪、日志） | 遵循 OpenTelemetry 协议                              | 接入 SkyWalking                                                      |

### 开发方式


|      | ZeroWeb                                                                                                                                                                        | [RuoYi-Vue-Pro](https://gitee.com/zhijiantianya/ruoyi-vue-pro) |
|------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------|
| 模块划分 | 将不同的领域分为不同的模块。每个模块构建为独立的 Docker 镜像。领域之间的交互极少。                                                                                                                                  | 将不同的领域划分为不同的模块。在上帝模块中引入需要的模块。                                  |
| 包划分  | 按功能分包。功能与功能之间通过有限知识的接口进行交互（高内聚低耦合）。可以很轻松地将一个功能迁移到其他模块。                                                                                                                         | 同一模块内按 MVC 层次分包。                                               |
| 单元测试 | [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=xezzon_zeroweb-spring&metric=coverage)](https://sonarcloud.io/summary/new_code?id=xezzon_zeroweb-spring) | N/A                                                            |

## [开发者手册](./CONTRIBUTING.md)

## [技术栈](https://xezzon.github.io/zeroweb-spring/dependencies.html)

## [License](https://xezzon.github.io/zeroweb-spring/licenses.html)

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fxezzon%2Fzeroweb-spring.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Fxezzon%2Fzeroweb-spring?ref=badge_large)
