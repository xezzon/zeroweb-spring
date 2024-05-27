# 开发者手册

## 项目结构

- `geom-proto`: 通过protobuf定义的服务间交互的结构体与接口。
- `geom-sdk`: 服务间接口的客户端SDK。
- `geom-spring-boot-starter`: 所有服务间共享的配置与工具。
- `geom-service`: 包含若干子模块，每一个模块是一个微服务构件。
  - `geom-service-admin`: 后台管理服务

### 包名

本项目的基础包为`io.github.xezzon.geom`，采用结构化设计。目录结构如下：

```
geom-service-admin
├── src
│   ├── main
│   │   ├── java
│   │   │   └── io/github/xezzon/geom
│   │   │       ├── user  # 用户功能
│   │   │       │   ├── UserService.java  # 该功能的逻辑运转中枢
│   │   │       │   ├── UserHttpController.java  # 提供前端调用的 HTTP 接口
│   │   │       │   ├── UserGrpcController.java  # 服务间调用的 GRPC 接口服务端
│   │   │       │   ├── domain  # 模型
│   │   │       │   ├── convert  # MapStruct 接口
│   │   │       │   ├── repository  # DAO 接口
│   │   │       │   ├── service   # 向其他包提供的功能的接口定义
│   │   │       │   │   └── IUserService4Group   # 向用户组提供的接口
│   │   │       │   └── README.md  # 用户功能的详细设计文档
│   │   │       ├── group  # 用户组
│   │   │       └── AdminApplication.java  # 启动类
│   │   └── resources
│   │       └── config
│   └── test  # 单元测试代码
│       ├── java
│       └── resources
└── target
    └── generated-sources  # 由 Maven 插件生成的代码
```

## 本地运行

## 工程规范

Project：需求、Bug 管理的各种视图。每一个功能对应一个 Project。可关联多个仓库。

Milestone：构件的版本号。按语义化版本，取`主版本号.次版本号`，修订版本号不参与命名。

Issue：需求或线上Bug。

Discussion：使用过程中的疑问或对新功能的可行性讨论。

### 提需求

需求包括新功能、原有功能的新需求、原有需求变更等。

角色：所有人

视图：[New issue](https://github.com/xezzon/geom-spring-boot/issues/new?assignees=&labels=feature&projects=&template=feature_request.md)

操作：撰写系统需求规格说明书。

### 提Bug

角色：所有人

视图：[New issue](https://github.com/xezzon/geom-spring-boot/issues/new?assignees=&labels=bug&projects=&template=bug_report.yml)

操作：填写运行环境、复现步骤等表单项。

### issue评审

角色：需求管理员

视图：[Issues](https://github.com/xezzon/geom-spring-boot/issues)

#### 评审通过

关联Milestone，关联Project，选择优先级。
完善需求规格说明书，将其中的术语定义与概览图复制到Project的README。

#### 驳回

选择标签`duplicate`/`invalid`/`wontfix`。 关闭issue `Close as not planned`。

### 任务分配

角色：软件开发人员

视图：[Projects](https://github.com/xezzon/geom-spring-boot/projects)/[Project]/Overview

操作：分配人员Assignees。将状态改为`In Progress`。

### Pull Request

角色：软件开发人员

视图：[New pull request](https://github.com/xezzon/geom-spring-boot/pulls)

操作：完成 [PR Checklist](.github/pull_request_template.md)。

### 代码评审

角色：所有人

视图：[Projects](https://github.com/xezzon/geom-spring-boot/projects)/[Project]/Overview

操作：评审代码。合并代码。

### 软件发布

角色：项目管理员

视图：[Create a new release](https://github.com/xezzon/geom-spring-boot/releases/new)

操作：新建标签，标签命名规则遵循语义化版本。
`Generate release note`，生成`feature`、`bug`标签的PR的更新日志。如果有其他说明如破坏性变更，则将其完善。
Target选择`main`，`Publish release`。
归档Project中非`feature`标签的issue。

## 代码规范

### 引用文档

- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [阿里巴巴《Java开发手册》](https://github.com/alibaba/p3c/)
- [Microsoft REST API Guidelines](https://github.com/microsoft/api-guidelines)

### 接口设计规范

#### 前后端接口

- 前后端交互的接口采用HTTP协议，遵顼RESTful命名规范。
- 路径命名法采用Kebab case。
- 路径参数都要放在第奇数位。
- 不能简单地表示为增删查改的复杂指令，非幂等的可以用`POST`方法，幂等的使用`PUT`方法。
- 更新、删除接口无需返回值（即返回类型为void）。标准新增接口的返回值为：`{"id":""}`，将新增后对象的ID返回，便于进行自动化测试。查询接口的返回值应为对象/对象列表/分页对象。响应码为`200`。
- 客户端异常（即需要用户处理的异常）响应码为`400`，服务端异常或第三方服务异常响应码为`500`。响应体结构为`{"code":"","message":"","data":{}}`。

以下是合适的接口示例：

- `GET /dict/id/{id}` 根据ID查询字典
- `GET /dict/page?filter=&pageNum=&pageSize=` 查询字典列表（分页）
- `GET /dict/tag/{tag}/code/{code}` 根据字典目、字典码查询字典
- `GET /dict/tag-code?tag=&code=` 根据字典目、字典目查询字典（风格二）
- `DELETE /dict/id/{id}` 根据ID删除字典
- `POST /dict/add` 新增字典
- `PUT /dict/update` 整体更新字典
- `POST /openapi/audit` 审核接口（审核接口会发起流程，所以是非幂等的）
- `PUT /openapi/cancel` 作废接口
