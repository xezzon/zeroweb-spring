# ZeroWeb

ZeroWeb 是一组 BaaS 微服务，用来低成本地实现认证授权、系统管理、开放平台与附件管理。本术语表记录这套系统内部一致使用的词汇；同一个概念在代码、文档、issue 中应使用这里的说法。

## Language

### 视角

**开发者**:
ZeroWeb 项目的开发维护人员。
_Avoid_: 作者、维护者（在需要区分三个视角时）

**使用者**:
集成 ZeroWeb 开发自己系统的开发人员。
_Avoid_: 用户、客户

**用户**:
系统最终的使用者。
_Avoid_: 客户、终端用户

### 认证与授权

**认证（Authn）**:
确认调用者身份的过程，包含用户名口令登录、JWT 验签、第三方应用摘要校验。
_Avoid_: 鉴权、登录校验

**授权（Authz）**:
判定已认证主体能否执行某操作，依据其角色与接口权限。
_Avoid_: 鉴权、权限校验

**Sa-Token Session**:
认证产生的服务端登录态，以 `X-SESSION-ID` 承载，默认有效期 30 天（`SA_TOKEN_TIMEOUT`）。
_Avoid_: Cookie 会话、登录票据

**JWT**:
跨服务传递调用者身份与权限的令牌，以 ES256 签名。载荷字段为 `sub`、`preferred_username`、`nickname`、`roles`、`entitlements`、`groups`、`exi`。
_Avoid_: Token、Session、凭证

**公钥头（`X-Public-Key`）**:
随请求下发的 JWT 验签公钥（Base64 编码的 DER），由网关从系统管理服务取得后回填给下游服务。
_Avoid_: 证书、密钥

**OIDC Token**:
登录或换取 SSO 令牌时的响应体，含 `access_token`（即 Sa-Token Session 的值）、`id_token`（JWT）与 `expires_in`。
_Avoid_: 登录响应、token（单独使用）

**AccessKey**:
第三方应用的公开标识，取值是第三方应用 ID 的 Base64 编码。
_Avoid_: 应用 ID、AppKey

**SecretKey**:
第三方应用的对称密钥，用于生成请求摘要，也用于签名该应用的 JWT。
_Avoid_: 密码、私钥

**AccessSecret**:
保存第三方应用 SecretKey 的实体，与第三方应用共用 `zeroweb_third_party_app` 表。
_Avoid_: 凭据、密钥对

**摘要（`X-Signature`）**:
请求体与毫秒时间戳拼接后的 HmacSHA256 值，用于证明请求来自持有 SecretKey 的应用。
_Avoid_: 签名、token

**邀请码**:
用第三方应用 SecretKey 签名的 JWT，载荷为应用 ID 与可选的被邀请人 ID，用于加入第三方应用成员。
_Avoid_: 邀请链接、token

### 用户与角色

**用户（User）**:
账号实体，`username` 全局唯一，`nickname` 用于界面展示，`cipher` 保存口令散列。
_Avoid_: 账号、账户、member

**口令**:
用户用于认证的原始秘密，数据库中只保存其 BCrypt 散列（`User.cipher`）。
_Avoid_: 密码（`zeroweb-proto` 的 `user.proto` 中仍写作「密码」）

**角色（Role）**:
权限的集合，以树形组织，并决定是否允许创建下级角色（`inheritable`）。`code` 是同级唯一的角色简码，`value` 是形如 `ADMIN/SYSTEM` 的完整角色编码。
_Avoid_: 角色名（指代 `value` 时）、角色编码（指代 `name` 时）

**内置角色**:
系统预置、不参与继承校验的角色：`ROOT`（超级管理员，id=3）、`ADMIN`（系统管理员，id=1）、`SUPER`（应用管理员，id=2）。
_Avoid_: 默认角色、系统角色

**接口权限**:
形如 `resource:operation` 的操作许可，如 `dict:write`；在 JWT 中以 `entitlements` 承载。
_Avoid_: 菜单权限、功能权限、API 权限

**资源权限**:
形如 `resource:#:operation` 的许可，`#` 处填入具体资源 ID，如 `third-party-app:#:invite-member`。
_Avoid_: 数据权限、对象权限

**用户组（Group）**:
把权限限定到某个资源实例的主体。目前没有独立实体：第三方应用即用户组，成员记录的 `groupId` 保存应用 ID；JWT 的 `groups` 载荷目前恒为空。
_Avoid_: 组织、部门、租户

### 字典

**字典（Dict）**:
一组提供给用户选择的值，由 `tag`/`code`/`label` 与树形结构描述。
_Avoid_: 枚举、常量表、数据字典

**字典目（tag）**:
字典的命名空间。字典目本身也是一条字典项，其 `code` 为 `DICT`。
_Avoid_: 字典类型、字典分组

**字典键（code）**:
同一字典目下唯一的键。约定：用户定义的键以小写字母开头，系统生成的键以大写字母开头。
_Avoid_: 字典编码、字典值

**字典值（label）**:
字典键对应的显示文本。
_Avoid_: 字典名称

**字典导入**:
把代码中以 `IDict` 枚举声明的字典在应用启动时写入数据库的过程。系统管理服务本地落库，其他服务经 gRPC 提交。
_Avoid_: 初始化字典、同步字典

### 业务参数

**业务参数（Setting）**:
运行期可调整的业务配置，由唯一 `code`、JSON Schema 约束（`schema`）与 JSON 值（`value`）组成。
_Avoid_: 系统设置、配置项、参数管理

**参数标识（code）**:
业务参数的唯一编码，如 `system.theme`，创建后不可修改。
_Avoid_: 参数名、键

### 开放平台

**第三方应用（Third Party App）**:
接入 ZeroWeb 开放接口的外部系统，`ownerId` 保存其所有者的用户 ID。
_Avoid_: 应用、客户端、合作方

**对外接口（Openapi）**:
由 `code`（对外路径）、`destination`（后端地址）、`httpMethod` 与发布状态描述的转发规则。
_Avoid_: 开放接口、API、路由

**订阅（Subscription）**:
第三方应用对某个已发布对外接口的调用许可；未订阅则不能调用。
_Avoid_: 授权、绑定、申请

**调用（Call）**:
第三方应用以摘要换取 JWT 后，经开放平台服务转发到对外接口目标地址的过程。
_Avoid_: 代理、透传

**成员（ThirdPartyAppMember）**:
用户在某个第三方应用中的身份，`groupId` 保存应用 ID，`roleId` 取 `OWNER_ROLE_ID`（`"1"`）或 `DEFAULT_ROLE_ID`（`"0"`）。
_Avoid_: 参与者、协作者

**所有者（Owner）**:
第三方应用中唯一持有 `OWNER_ROLE_ID` 的成员，可把所有权转移给其他成员。
_Avoid_: 创建人、管理员

### 附件与存储

**附件（Attachment）**:
与业务数据关联的文件元数据记录，通过 `bizType` 与 `bizId` 挂到业务实体上。
_Avoid_: 文件（指代附件记录时；「文件」指存储中的字节内容）

**内容摘要（checksum）**:
附件内容的 SHA-256（Base64），用于校验续传内容一致与文件完整性。
_Avoid_: 签名、CRC

**CRC**:
分片校验值，仅 S3 后端在分片上传时需要，以查询参数传入。
_Avoid_: checksum、摘要

**对象键（objectKey）**:
附件在存储后端中的路径，格式为 `yyyy/MM/dd/{附件ID}`，日期取创建时间的 UTC 值。
_Avoid_: 文件名、存储路径

**存储后端（provider）**:
附件的实际存储实现，取 `FS`（本地硬盘）或 `S3`（兼容 S3 的对象存储），在创建附件时固定。
_Avoid_: 存储类型、驱动

**上传元信息（UploadInfo）**:
新增附件或断点续传时返回的分片数量与分片大小。
_Avoid_: 上传配置

**上传地址（UploadEndpoint）**:
客户端直接上传文件内容的目标地址。`partNumber` 为 0 表示整体上传，大于 0 表示第几个分片。
_Avoid_: 上传接口

**下载地址（DownloadEndpoint）**:
客户端获取文件内容的地址，含文件名。
_Avoid_: 下载接口

### 国际化

**语言（Language）**:
可用的界面语言，`languageTag` 采用 `Accept-Language` 风格的标签（如 `zh-CN`）。语言以与字典相同的结构存储，但表由研发平台服务自己持有。
_Avoid_: locale、翻译、语种

**国际化内容（I18nMessage）**:
一条待翻译的文案，由 `namespace` 与 `messageKey` 唯一定位。
_Avoid_: 翻译、词条、资源

**翻译文本（Translation）**:
某条国际化内容在某种语言下的实际文本，由 `namespace`+`messageKey`+`language` 确定。
_Avoid_: 译文、语言包

**命名空间（namespace）**:
国际化内容的逻辑分组，用于按模块或页面批量检索文案。
_Avoid_: 模块、分组

### 服务与部署

**系统管理服务**:
`zeroweb-service-admin`。负责认证、单点登录、角色与接口权限、用户、字典与业务参数。
_Avoid_: admin 服务、权限服务

**开放平台服务**:
`zeroweb-service-open`。负责第三方应用、对外接口、订阅与调用转发。
_Avoid_: open 服务、网关（网关另有所指）

**附件管理服务**:
`zeroweb-service-file`。集中管理所有业务的附件。
_Avoid_: 文件服务、file 服务

**研发平台服务**:
`zeroweb-service-dev`。负责国际化，即语言、国际化内容与翻译文本。
_Avoid_: 开发平台服务、dev 服务

**网关**:
部署在各服务之前的 Traefik 入口，负责向系统管理服务换取 JWT 与公钥头后转发请求（forwardAuth）。
_Avoid_: API 网关、反向代理（指代本项目的入口时）

**服务自省**:
服务通过 `GET /metadata/info.json` 与 `GET /metadata/menu.json` 暴露自身名称、版本与所辖菜单和权限的机制。
_Avoid_: 服务注册、服务发现

**菜单（MenuInfo）**:
服务自省返回的一项资源描述，其类型由 `MenuType` 决定：`ROUTE`（前端路由）、`EXTERNAL_LINK`（外部链接）、`EMBEDDED`（嵌入页面）、`PERMISSION`（接口权限）、`GROUP_PERMISSION`（资源权限）。
_Avoid_: 导航、页面、菜单项

**服务类型（ServiceType）**:
服务的部署角色，`SERVER` 为后端服务，`CLIENT` 为前端。
_Avoid_: 客户端、服务端（`CLIENT` 指前端，`SERVER` 指后端）

**访问点（Endpoint）**:
服务对外暴露的一个 HTTP 或 gRPC 入口。访问点只调用 Service，不直接访问 DAO 或 Repository。
_Avoid_: Controller、Resource、接口（「接口」另有授权含义）

**功能接口**:
一个功能向另一个功能暴露的有限知识接口，命名为 `I<功能>Service4<调用方>`，如 `IUserService4Auth`。功能之间只允许依赖这类接口。
_Avoid_: 内部接口、共享 Service

### 模块与 SDK

**服务间接口 SDK**:
`zeroweb-proto`，以 protobuf 定义的服务间契约。JVM 使用者经 Maven 消费，其他语言经 buf 生成。
_Avoid_: proto 库、RPC SDK

**前后端接口 SDK**:
面向前端的 HTTP 接口封装，即 npm 包 `@xezzon/zeroweb-sdk`。
_Avoid_: 前端库

**开放平台 SDK**:
`zeroweb-open-sdk`，供开放平台的使用者封装第三方调用方的 OpenFeign 客户端，自动为请求附加 AccessKey、时间戳与摘要。
_Avoid_: 开发平台 SDK（该模块 README 的旧称）
