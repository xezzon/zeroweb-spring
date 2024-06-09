# 认证

## 用户登录

## 单点登录

### 网关模式（推荐）

```mermaid
sequenceDiagram
    participant client AS 客户端
    participant gateway AS 网关
    participant auth AS 认证中心
    participant service AS 服务端

    client ->> auth: 携带 Basic Token 的登录请求
    auth -->> client: 返回 X-SESSION-ID

    client->> gateway: HTTP请求（携带 X-SESSION-ID Header）
    gateway ->> auth: 转发到认证中心
    auth -->> gateway: 读 X-SESSION-ID 获取 JWT
    gateway ->> service: 将 JWT 和公钥分别写入Header（Authentication 和 X-PUBLIC-KEY）

    service -> service: 用公钥和时间戳校验 JWT
    service -> service: 从 JWT 中获取用户信息
    service -->> client: 返回请求结果
```

### Redis 共享模式

```mermaid
sequenceDiagram
    participant client AS 客户端
    participant auth AS 认证中心
    participant service AS 服务端
    participant redis As Redis
    
    client ->> auth: 携带 Basic Token 的登录请求
    auth ->> redis: 存入 Session 信息
    auth -->> client: 返回 X-SESSION-ID

    client ->> service: HTTP请求（携带 X-SESSION-ID Header）
    service ->> redis: 取出 Session 信息
    redis -->> service: 取出 Session 信息
    service -->> client: 返回请求结果
```

## 签发 [JWT](https://jwt.io/)

本系统签发的 JWT 采用 ES256 算法。

claim 命名参照 [IANA 机构](https://www.iana.org/assignments/jwt/jwt.xhtml) 内已注册的 claim，包含以下内容：

| 字段                 | 含义      | 注释                       |
|--------------------|---------|--------------------------|
| iss                | 机构名称    |                          |
| sub                | 认证者ID   |                          |
| iat                | 令牌签发时间戳 |                          |
| exp                | 令牌过期时间戳 | 如果当前时间戳大于exp，则令牌视为无效。选填。 |
| nbf                | 令牌生效时间  | 如果当前时间戳小于nbf，则令牌视为无效。选填。 |
| jti                | 令牌ID    | 生成的UUID。选填。              |
| preferred_username | 用户名     |                          |
| nickname           | 用户昵称    |                          |
| roles              | 持有者角色   | 类型为 String[]。            |
| groups             | 所在用户组   | 类型为 String[]。            |
| entitlements       | 持有者权限   | 类型为 String[]。            |
