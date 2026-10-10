# 第三方应用用摘要换取 JWT

第三方应用不持有 JWT，也不直接访问各微服务。调用方以 `X-Access-Key`、`X-Timestamp`、`X-Signature`（`HmacSHA256(body + 毫秒时间戳)`，密钥为 SecretKey）发起请求；开放平台服务校验摘要后，用该应用的 SecretKey 以 HS256 签发一个 `sub` 为应用 ID 的短时 JWT，再按订阅把请求转发到对外接口的 `destination`。下游服务收到的是标准 JWT，不需要理解开放平台的签名协议。

## Considered Options

- 第三方应用用 AccessKey/SecretKey 直接访问各服务，由各服务自行校验摘要。被否决：摘要协议会被复制到每个服务，且各服务要各自维护一份应用凭据。
- 为第三方应用签发长期 JWT。被否决：SecretKey 轮换后无法收回已签发的令牌。
