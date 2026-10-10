# 运行时配置只经环境变量

使用者需要修改的配置（数据库地址、缓存类型、存储后端、JWT 签发者与有效期等）只在 application 配置中以 `${ENV_VAR:default}` 的形式读取环境变量，不要求使用者改动镜像内的文件。不希望被修改的非运行时配置写入各模块自带的 `application-*.yml`；测试配置写入测试资源。新增的运行时配置必须同步写入所属模块的 README。

这样同一份镜像可以在测试、生产等环境运行，也天然兼容 Docker、K8s 与配置中心。代价是配置项分散在环境变量中，可发现性依赖文档；`SPRING_ENVIRONMENT=dev` 时由 Hibernate 更新表结构并禁用 Liquibase，与线上行为不同，这种差异也只由环境变量切换。

## Considered Options

- 让使用者编辑 `application.yml` 后重新打包。被否决：一个镜像只能对应一套配置，无法「一套代码到处运行」。
