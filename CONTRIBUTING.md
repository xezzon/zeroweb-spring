# 开发者指南

首先感谢每一位对本项目做出贡献的开发者。您的努力和智慧是项目成功的关键。

本文档希望能帮助您写出高质量的、运维友好的设计与代码，更好地参与本项目的开发。

## 项目结构

- `zeroweb-proto`: 通过protobuf定义的服务间交互的结构体与接口。
- `zeroweb-spring-boot-starter`: 所有服务间共享的配置与工具。
- `zeroweb-service`: 包含若干子模块，每一个模块是一个微服务构件。
  - `zeroweb-service-admin`: 后台管理服务
  - `zeroweb-service-open`: 开放平台服务

### 包名

本项目的基础包为`io.github.xezzon.zeroweb`，采用结构化设计。目录结构如下：

```
zeroweb-service-admin
├── src
│   ├── main
│   │   ├── java
│   │   │   └── io/github/xezzon/zeroweb
│   │   │       ├── user  # 用户功能
│   │   │       │   ├── UserService.java  # 该功能的逻辑运转中枢
│   │   │       │   ├── UserHttpController.java  # 提供前端调用的 HTTP 接口
│   │   │       │   ├── UserGrpcServer.java  # 服务间调用的 GRPC 接口服务端
│   │   │       │   ├── domain  # 充血模型。
│   │   │       │   ├── entity  # 贫血模型。
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

### 前置条件

开始开发前，请保证以下开发环境已经安装完成：

- [Git](https://git-scm.com/downloads)
- [OpenJDK 17](https://adoptium.net/zh-CN/temurin/releases/?version=17&package=jdk)
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

`TODO`

### 引用文档

- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [阿里巴巴《Java开发手册》](https://github.com/alibaba/p3c/)
- [Microsoft REST API Guidelines](https://github.com/microsoft/api-guidelines)
