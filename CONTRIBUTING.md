# 开发者指南

首先感谢每一位对本项目做出贡献的开发者。您的努力和智慧是项目成功的关键。

本文档希望能帮助您写出高质量的、运维友好的设计与代码，更好地参与本项目的开发。

想要快速了解本项目，可以借助[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/xezzon/zeroweb-spring)（仅供参考）。

> 约定：本项目的所用文档中，开发者称呼 ZeroWeb 项目的开发维护人员，使用者称呼集成 ZeroWeb 开发自己系统的开发人员，用户称呼系统最终的使用者。

## 项目结构

### 模块

- `zeroweb-proto`: 通过protobuf定义的服务间交互的结构体与接口。
- `zeroweb-spring-boot-starter`: 所有服务间共享的配置与工具。
- `zeroweb-service`: 包含若干子模块，每一个模块是一个微服务构件。
  - `zeroweb-service-admin`: 系统管理服务。
  - `zeroweb-service-dev`: 研发平台服务。
  - `zeroweb-service-file`: 附件管理服务。
  - `zeroweb-service-open`: 开放平台服务。

### 包名

本项目的基础包为`io.github.xezzon.zeroweb`，采用结构化设计。

### 模块目录结构

#### zeroweb-spring-boot-starter

```
zeroweb-spring-boot-starter
├── README.md  # 模块说明文档
├── src
│   └── main
│       ├── java
│       │   └── io/github/xezzon/zeroweb
│       │       │── auth  # 认证相关的过滤器
│       │       │── common
│       │       │       │── config  # Spring Boot 配置类
│       │       │       └── exception  # 全局异常处理
│       │       └── core  # 与 Spring 无关的代码结构
│       └── resources
│           │── config  # 全局的 Spring Boot 配置文件
│           └── i18n  # 国际化资源文件
└── pom.xml  # 依赖文件
```

#### zeroweb-service

```
zeroweb-service-admin
├── README.md  # 模块说明文档
├── doc
│   └── openapi.yaml  # 接口设计文档
├── src
│   ├── main
│   │   ├── java
│   │   │   └── io/github/xezzon/zeroweb
│   │   │       ├── user  # 用户功能
│   │   │       │   ├── UserService.java  # 该功能的逻辑运转中枢
│   │   │       │   ├── UserHttpEndpoint.java  # 提供前端调用的 HTTP 接口
│   │   │       │   ├── UserGrpcEndpoint.java  # 服务间调用的 gRPC 接口服务端
│   │   │       │   ├── UserDAO.java  # 对 JPA 接口的封装
│   │   │       │   ├── domain  # JPA Entity、枚举类
│   │   │       │   ├── entity  # 贫血模型
│   │   │       │   ├── convert  # MapStruct 接口
│   │   │       │   ├── repository  # JPA 接口
│   │   │       │   ├── service   # 向其他包提供的功能的接口定义
│   │   │       │   │   └── IUserService4Group   # 向用户组提供的接口
│   │   │       │   └── README.md  # 功能描述文档
│   │   │       ├── group  # 用户组功能
│   │   │       └── AdminApplication.java  # 启动类
│   │   └── resources
│   │       ├── config  # Spring Boot 配置文件
│   │       ├── db
│   │       │   └── changelog  # 数据库变更文件
│   │       └── i18n  # 国际化资源文件
│   └── test  # 单元测试代码
│       ├── java
│       └── resources
├── pom.xml  # 依赖文件
└── target
    └── generated-sources  # 由 Maven 插件生成的代码
```

## 本地运行

### 前置条件

开始开发前，请保证以下开发环境已经安装完成：

- [Git](https://git-scm.com/downloads)
- [OpenJDK 25](https://adoptium.net/zh-CN/temurin/releases/?version=25&package=jdk)
- [Maven](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com)/[Podman](https://podman.io/)

### 获取项目源代码

```shell
git clone https://github.com/xezzon/zeroweb-spring.git
```

### 项目配置文件

详细配置项请查看各服务的说明，示例如下：

```properties
# .local.env
SPRING_ENVIRONMENT=dev
JDBC_TYPE=postgresql
```

主流IDE的配置方法请查看对应的链接：[IDEA](https://www.jetbrains.com/help/idea/run-debug-configuration-java-application.html#more_options)、[Eclipse](https://help.eclipse.org/latest/topic/org.eclipse.jdt.doc.user/tasks/tasks-java-local-configuration.htm?cp=1_3_6_3)、[VSCode](https://code.visualstudio.com/docs/java/java-debugging)。

### 运行服务

依据[项目结构](#项目结构)所示，运行服务的启动类。

## 工程规范

`TODO`

## 开发规范

### 遵循社区公认的、成熟的解决方案与最佳实践

- 例1：
  - 反例：所有 HTTP 请求一律使用 POST 方法。响应一律使用 200 响应状态码。
  - 分析：无法在网关处按请求方法区分是否需要做幂等控制；需要自行实现对响应体的分析。吃不到社区的红利。
  - 正例：按 RESTful 规范使用 HTTP 请求方法。异常响应使用 4xx 或 500 响应状态码。
- 例2：
  - 反例：使用 ECharts-Java 生成图表。
  - 分析：依赖于非官方维护的项目，难以获得最好的社区支持，容易踩坑。
  - 正例：使用 NodeJS + ECharts 为技术栈的微服务，调用其提供的 HTTP 端点获得图表。

### 设计原则优先于设计模式

- 遵循 SOLID原则、KISS原则、YAGNI原则、DRY原则、迪米特法则等设计原则。它们相当于代码界的公理，并不局限于语言或编程范式。遵循这些设计原则更容易写出高质量的代码。
- 遵循面向接口编程、组合优于继承、约定优于配置等社区公认的实践经验。

### 结构化编程

以 [dict](./zeroweb-service/zeroweb-service-admin/src/main/java/io/github/xezzon/zeroweb/dict)
功能为模板。

#### JPA Entity

- 实现 `io.github.xezzon.zeroweb.common.jpa.IEntity` interface，使用 `lombok.Getter`、`lombok.Setter`、
  `lombok.ToString`、`jakarta.persistence.Entity`、`jakarta.persistence.Table` 注解。
- 对所有字段使用合适的 `jakarta.persistence` 注解，如 `jakarta.persistence.Column`、
  `jakarta.persistence.Transient`。并填充合适的属性。
- 如果需要实现乐观锁，则使用 `updateTime` 字段。如果需要实现逻辑删除，则使用 `deleteTime` 字段。
- 鼓励将 JPA Entity 作为充血模型。
  反例：在 Service 中判断接口发布状态 `openapi.getStatus() == OpenapiStatus.PUBLISH`。
  正例：将逻辑封装到 `openapi.isPublished()` 方法。

#### Service

- Service 是一个普通类。使用 `org.springframework.stereotype.Service` 注解。
- Service 类中提供给 HTTP 端点 和 gRPC 端点调用的方法的访问级别为 `protected`；Service
  内部使用的方法访问级别为 `private`；
- 提供给其他 Service 调用的方法需要在 `service` 包下定义 interface，并在 Service 类中实现。提供给不同
  Service 调用的方法需要在不同的 interface 中定义。不允许直接注入其他 Service，而应该依赖其对应的
  interface。

#### HTTP 端点 和 gRPC 端点

- HTTP 端点使用 `org.springframework.stereotype.RestController` 注解。
- gRPC 端点使用 `org.springframework.grpc.server.service.GrpcService` 注解。
- 两者均只允许被注入 Service，不允许直接注入 DAO 或 Repository。
- gRPC 端点的原型在 `zeroweb-proto` 模块定义。
- 一般情况下，应该对每个复杂请求封装一个请求对象，将其转换为对应的充血模型传给 Service 层。
- 一般情况下，可以直接以充血模型作为响应对象。必要时也可以封装一个响应对象。
- 分页查询支持 odata 语法。参数为 `io.github.xezzon.zeroweb.core.odata.ODataRequestParam`，通过
  `io.github.xezzon.tao.trait.Into#into()` 方法转换为
  `io.github.xezzon.zeroweb.core.odata.ODataQueryOption` 传给 Service。返回值为
  `org.springframework.data.domain.Page`。

#### 贫血模型

贫血模型的种类有很多。比如对请求的封装、对响应的封装、事件对象。

- 请求对象，一个 record 类。作为 HTTP 端点的参数。实现 `io.github.xezzon.tao.trait.Into`
  接口，可以以自身为参数返回充血模型。可以实现一个范围级别为 `package-private` 的类内 interface，继承
  `io.github.xezzon.tao.trait.From` 接口，使用 `org.mapstruct.Mapper`
  注解。请求对象中的各字段应该使用合适的 [Hibernate Validator](https://hibernate.org/validator/) 注解。
- 响应对象，一个 record 类。作为 HTTP 端点的返回。实现一个名为 `from` 的静态方法，将充血模型转换为自身。可以实现一个范围级别为
  `package-private` 的类内 interface，继承 `io.github.xezzon.tao.trait.From` 接口，使用
  `org.mapstruct.Mapper` 注解。
- 事件对象，使用 `lombok.Builder` 注解。

#### Repository

- Repository 是一个 interface，继承 `org.springframework.data.jpa.repository.JpaRepository` 和
  `org.springframework.data.jpa.repository.JpaSpecificationExecutor`，使用
  `org.springframework.stereotype.Repository` 注解。

#### DAO

- DAO 是一个普通类，继承 `io.github.xezzon.zeroweb.common.jpa.BaseDAO`，使用
  `org.springframework.stereotype.Repository` 注解，对 Repository 进行了封装。
- DAO
  中可以使用 [JPA Specification](https://docs.spring.io/spring-data/jpa/reference/jpa/specifications.html)
  对数据库进行操作。
- 所有对数据库的直接操作都必须在 DAO 或 Repository 中完成。

### 配置

- 注意区分不同的配置：
  - 非运行时配置。不希望使用者和用户修改的配置，例如 Session Id 的风格。写在配置文件
    `src/main/resources/config/application[-mode].yml`中。
  - 测试配置。单元测试时使用的配置，例如 Testcontainers 的数据库地址。写在配置文件
    `src/test/resources/application.yml` 中。
  - 运行时配置。使用者在运行服务时应该配置的内容，配置后不能轻易变更，例如服务连接的数据库地址。定义在
    application 配置文件中，通过 `${ENVIRONMENT_VARIABLE}` 的格式读取环境变量，这是 Docker 和 K8s 友好的。
- 自定义的配置需要在 `io.github.xezzon.zeroweb.common.config.ZerowebConfig` 类中定义。
- 运行时配置需要写入模块说明文档。如果是所有模块公共的配置，则写入`zeroweb-spring-boot-starter`
  模块的说明文档。
- Spring Boot 可以从 `pom.xml` 中读取配置。Maven 在编译时会将配置文件中的 `@property.name@`
  静态地替换。然而并不鼓励将大部分配置写在 `pom.xml` 中。

### 数据库版本控制

- 数据库版本控制技术栈为 [Liquibase](https://www.liquibase.com/)。
- 变更文件格式采用 xml 格式。
- 每一个数据表对应一个变更文件，以便追溯每一个表的变更记录。如 user.xml 文件对应 zeroweb_user 表的变更记录。
- 变更集（changeSet）的 `id` 取值为 issue 编号；`author` 取值为变更人的 Git 提交名称；`label` 取值为
  issue 对应的里程碑。

### 单元测试

- 单元测试技术栈为 [JUnit 5](https://junit.org/junit5/) 。
- 单元测试的对象主要是 HTTP 端点和 gRPC 端点，存在测试未覆盖到的情况时，可以针对对应的类进行测试。
- 针对 HTTP 端点的测试命名方式为 `${功能名称}HttpTest.java`
  。通过注入 [WebTestClient](https://docs.spring.io/spring-framework/reference/testing/webtestclient.html)
  调用 HTTP 端口进行测试。
- 针对 gRPC 端点的测试命名方式为 `${功能名称}GrpcTest.java`。通过注入对应的 gRPC Stub 调用 gRPC
  端点进行测试。
- 其他类的测试命名方式为`${类名}Test.java`。
- 测试类的方法命名为`${被测试的方法名}_${预期的情况}`。测试类与测试方法的访问级别
- 所有对中间件与外部系统的依赖都通过 [Testcontainers for Java](https://java.testcontainers.org/) 解决。严禁使用任何 Mock 方法或框架进行单元测试。
- 所有依赖于 Spring Boot 的测试类，都需要用 `@SpringBootTest`（如果是针对 HTTP 端点的测试，则是
  `@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)`） 和 `@DirtiesContext` 注解。
- 如果需要，所有方法测试（@BeforeEach）前向相关的数据表中写入随机的测试数据，测试方法结束后（@AfterEach）将数据表中的数据全部删除。

### CI/CD

- CI/CD 技术栈为 [GitHub Actions](https://docs.github.com/actions)
  。配置文件请查看[./.github/workflows](./.github/workflows)。

### HTTP 接口设计

- API 设计遵循 [RESTful API](https://github.com/microsoft/api-guidelines) 规范。
- 未发生异常时应返回 2xx 或 3xx 类型的 HTTP 响应状态码。客户端的错误按语义使用正确的 HTTP
  响应状态码，如未认证返回 401，未授权返回 403，请求过快返回 429。客户端一般错误使用 400，服务端错误统一使用
  500。
- 发生异常时，采用统一的异常返回格式。正常返回无需统一格式。
- 开发前，先使用 API 设计工具导出对应 yaml 格式的 OpenAPI 文档（包含异常场景）。存储于各服务模块的
  `docs/openapi.yaml` 中。
- 使用 [Springdoc](https://springdoc.org/) 基于 JavaDoc 生成 OpenAPI 文档。禁止使用注解生成。

### 遥测

- 遥测技术栈为 [OpenTelemetry](https://opentelemetry.io/)。
- 对请求和调用栈的记录通过 Trace 解决。对重要操作使用 Slf4j 记录日志。两者不能混为一谈。

### 代码风格

- 代码风格参考 [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
  和 [阿里巴巴《Java开发手册》](https://github.com/alibaba/p3c/)。
- 依赖注入使用构造器的方式。
- 时间类型优先使用 `java.time.Instant`。如果有需要使用 `java.time.LocalDateTime` 的情形需要进行说明。

### 依赖管理

- 一个新版本发布前，需要先更新其依赖。
- 请谨慎地添加任何一个新的依赖/插件。
