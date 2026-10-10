# 领域即模块，模块即构件

每个限界上下文就是一个可独立发布的构件，而不是一个聚合工程里的文件夹。基础模块（`zeroweb-proto`、`zeroweb-spring-boot-starter`、`zeroweb-open-sdk`）各自是独立 Maven 工程，单独发布到 Maven Central；四个微服务由 `zeroweb-service` 聚合以便共享依赖与插件配置，但每个服务是独立构件、拥有独立版本号，产物是 Docker 镜像（jib 推送 GHCR），不提供聚合 jar。

关键在于依赖解析方式：服务并不通过 reactor 消费彼此或基础模块，而是在 pom 中以属性声明 Maven Central 上已发布的版本（如 `zeroweb-proto.version`）。这样每个服务都能被独立测试、构建、部署，非 JVM 或非 ZeroWeb 的项目也可以只依赖 proto 契约而不拉入实现。代价是改动一个模块后必须先发版才能被其他模块消费，且需要 CI 校验各模块版本号与主分支一致。
