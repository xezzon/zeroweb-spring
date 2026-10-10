# 测试针对访问点，不使用 Mock

模块的对外契约是 HTTP 访问点与 gRPC 访问点，因此单元测试的对象也是它们（`*HttpTest` 用 WebTestClient，`*GrpcTest` 用生成的 Stub）。所依赖的中间件与外部系统一律用 Testcontainers 启动真实实例；只有 Service 之间协作所需的接口可以实现 Mock 类，其他位置禁止使用任何 Mock 框架。

内部实现被当作黑盒，才能在不改测试的前提下重构；Mock 掉依赖会让「测试通过」与「功能可用」脱钩。
