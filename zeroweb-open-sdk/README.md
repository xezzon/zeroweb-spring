# 开发平台 SDK

该项目包含一个 OpenFeign 拦截器的构造类，用于在发送请求前自动构建带有认证信息的 OpenFeign 客户端。

zeroweb-service-open（开放平台微服务）的使用者希望对其向第三方应用暴露的 HTTP 接口封装 Java SDK 时，可以通过此 SDK 快速构建带有认证信息的 OpenFeign 客户端。

## 安装

### 通过 Maven 安装

```xml

<dependency>
  <groupId>com.auth0</groupId>
  <artifactId>java-jwt</artifactId>
  <version>4.4.0</version>
</dependency>
```

## 使用

使用方法参考[单元测试](./src/test/java/io/github/xezzon/zeroweb/ZerowebOpenRequestBuilderTest.java)。
