# JWT 是跨服务的凭证

微服务可以异构，因此认证结果不能以「查询中心认证服务」的形式传播。认证结果一律以 JWT 表达：网关（Traefik forwardAuth）调用系统管理服务的 `GET /auth/self` 取得 JWT 与验签公钥，分别写入 `Authorization` 与 `X-Public-Key` 请求头；下游服务各自用该公钥本地验签，权限判定直接读取 JWT 载荷中的角色与权限（`JwtStpInterface`），不再为每次请求查库。服务间 gRPC 调用通过 Metadata 透传同一个 claim（`GrpcJwtInterceptor`）。

## Considered Options

- 下游服务每次鉴权都回调系统管理服务。被否决：让所有服务都依赖中心服务，违背服务可独立部署的前提，也带来每请求一次额外跳转。
- 所有服务共享 Session 存储（如 Redis）读取登录态。被否决：把「异构语言集成」变成了「必须接同一个 KV」。

## Consequences

JWT 有效期内（默认 120 秒）权限变更不会即时生效，也无法即时吊销单个令牌。安全要求更高的场景只能缩短 `ZEROWEB_JWT_TIMEOUT`。
