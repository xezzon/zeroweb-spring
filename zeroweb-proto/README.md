# 服务间接口SDK

该项目是以 gRPC 协议定义的 ZeroWeb 服务接口定义，同时为 JVM 用户提供官方 SDK。

对于非 JVM 用户，可通过 `buf.gen.yaml` 配合 GitHub 仓库 + 标签的方式消费本项目中的 proto 定义，生成对应语言的客户端代码。

## 安装

### 通过 Maven 安装（JVM）

```xml
<dependency>
  <groupId>io.xezzon.github</groupId>
  <artifactId>zeroweb-proto</artifactId>
  <version>${zeroweb-proto.version}</version>
</dependency>
```

### 通过 buf 生成代码（其他语言）

proto 定义位于 `zeroweb-proto/proto` 目录，发布时通过形如 `proto/v[semver]` 的 Git 标签触发（详见 `.github/workflows/release.yml`）。

消费者可通过 `buf.gen.yaml` 调用对应语言的插件生成代码。

基本流程：

1. 在项目根目录创建 `buf.gen.yaml`，声明远程 proto 输入与目标语言的代码生成插件
2. 执行 `buf generate` 生成代码
3. 声明对应语言所需的运行时依赖

#### Rust

推荐的 gRPC 实现是 [tonic](https://github.com/grpc/grpc-rust)，配合 `prost` 生成消息体。

```yaml
# buf.gen.yaml
version: v2
inputs:
  - git_repo: https://github.com/xezzon/zeroweb-spring.git
    ref: proto/v0.11.0         # 替换为所需的标签
    subdir: zeroweb-proto/proto
plugins:
  - remote: buf.build/protocolbuffers/rust
    out: src
  - remote: buf.build/community/neoeinstein-tonic
    out: src
```

执行 `buf generate` 生成 Rust 代码到 `src/` 目录。

在 `Cargo.toml` 中声明运行时依赖：

```toml
[dependencies]
tonic = "0.12"
prost = "0.13"
```

使用示例：

```rust
use io::github::xezzon::zeroweb::auth::authentication_client::AuthenticationClient;
```

> 生成的模块路径由 proto 文件的 `package` 声明决定，`buf.build/protocolbuffers/rust` 会将其映射为 Rust module 层级。

#### NodeJS / TypeScript

推荐通过 [ts-proto](https://github.com/stephenh/ts-proto) 同时生成消息类型与 gRPC 客户端。

```yaml
# buf.gen.yaml
version: v2
inputs:
  - git_repo: https://github.com/xezzon/zeroweb-spring.git
    ref: proto/v0.11.0         # 替换为所需的标签
    subdir: zeroweb-proto/proto
plugins:
  - remote: buf.build/community/stephenh-ts-proto
    out: src/generated
    opt:
      - outputServices=grpc-js
      - esModuleInterop=true
      - useDate=true
```

执行 `buf generate` 生成 TypeScript 代码到 `src/generated/` 目录。

`package.json` 引入运行所需依赖：

```json
{
  "dependencies": {
    "@grpc/grpc-js": "^1.10.0",
    "google-protobuf": "^3.21.0"
  },
  "devDependencies": {
    "ts-proto": "^1.181.0"
  }
}
```

使用示例：

```typescript
import { AuthenticationClient } from "./generated/io/github/xezzon/zeroweb/auth/authentication";

const client = new AuthenticationClient("localhost:8080", /* credentials */);
```

> `ts-proto` 生成的输出路径与 proto 文件在模块中的相对路径一致。可通过 `paths=source_relative`（默认即此行为）控制。

#### Golang

由于 proto 文件未声明 `go_package` 选项，外部消费者需要通过 buf managed mode 统一指定生成代码的 Go 包前缀。

```yaml
# buf.gen.yaml
version: v2
inputs:
  - git_repo: https://github.com/xezzon/zeroweb-spring.git
    ref: proto/v0.11.0         # 替换为所需的标签
    subdir: zeroweb-proto/proto
managed:
  enabled: true
  override:
    - file_option: go_package_prefix
      value: github.com/your-org/your-project/gen  # 替换为你的工程路径
plugins:
  - remote: buf.build/protocolbuffers/go
    out: gen
    opt:
      - paths=source_relative
  - remote: buf.build/grpc/go
    out: gen
    opt:
      - paths=source_relative
```

执行 `buf generate` 生成 Go 代码到 `gen/` 目录。

Go 运行时依赖：

```bash
go get google.golang.org/grpc
go get google.golang.org/protobuf
```

使用示例：

```go
import authv1 "github.com/your-org/your-project/gen/io/github/xezzon/zeroweb/auth/v1"
```

> `managed` 模式会自动为所有 proto 文件注入 `go_package` 选项，值为 `<go_package_prefix>/<proto 文件相对模块路径>`。生成的 Go 代码通过 `paths=source_relative` 保持与 proto 文件相同的目录层级。
