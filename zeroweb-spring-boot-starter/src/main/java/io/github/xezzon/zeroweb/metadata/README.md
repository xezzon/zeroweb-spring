# 服务元数据自省

## 概述

服务元数据自省机制旨在 以牺牲一定的灵活性为代价，解决传统菜单管理中代码与数据分离、多环境数据迁移导致的复杂性与高风险问题。通过统一的 HTTP 接口，服务能够自动发布其元数据，包括基本信息和可分配的菜单结构，从而实现元数据驱动的动态管理。

## 目的

本机制的核心目的是**规避传统菜单管理中以下痛点：**

* **代码与数据分离带来的管理负担**：无需手动同步代码中的菜单定义与数据库中的菜单数据。
* **多环境数据迁移的复杂性与风险**：通过服务自省，菜单数据可以在不同环境中自动发现和注册，显著降低人工操作和潜在错误。
* **降低工作量与风险**：自动化元数据发现和注册流程，提升开发效率和系统稳定性。

## 用法

所有遵循本规范的服务（包括前端和后端服务）都必须在其可访问的根路径下实现以下两个 HTTP 接口：

* **`GET /metadata/info.json`**:
    * **提供内容**: 该接口应返回服务的通用元数据信息，例如服务名称、版本、描述等。这些信息可用于服务发现、健康检查或管理面板展示。
    * **响应示例（后端）**:
      ```json
      {
        "name": "your-service-name",
        "version": "0.1.0",
        "type": "SERVER",
        "hidden": true
      }
      ```
    * **响应示例（前端）**:
      ```json
      {
        "name": "your-app-name",
        "version": "1.0.0",
        "type": "CLIENT",
        "hidden": false
      }
      ```

* **`GET /metadata/menu.json`**:
    * **提供内容**: 该接口应返回该服务提供的、可分配的菜单结构。这包括菜单项的
      ID、名称、路径、图标以及层级关系等。这些信息将用于构建统一的权限和菜单管理系统。
    * **响应示例（后端）**:
      ```json
      [
        {
          "type": "PERMISSION",
          "path": "role:write",
          "permissions": ["role:write"]
        },
        {
          "type": "GROUP_PERMISSION",
          "path": "openapi:#:subscribe",
          "permissions": ["openapi:#:subscribe"]
        }
      ]
      ```
    * **响应示例（前端）**:
      ```json
      [
        {
          "type": "ROUTE",
          "path": "/",
          "name": "首页",
          "route": "_index"
        },
        {
          "type": "GROUP",
          "path": "app-management",
          "name": "应用管理"
        },
        {
          "type": "ROUTE",
          "path": "/app",
          "name": "应用注册",
          "route": "app/index",
          "parent": "app-management",
          "permissions": ["app:read"]
        },
        {
          "type": "ROUTE",
          "path": "/menu",
          "name": "应用菜单",
          "route": "menu/index",
          "parent": "app-management",
          "permissions": ["menu:read"]
        },
        {
          "type": "GROUP",
          "path": "setting",
          "name": "配置管理"
        },
        {
          "type": "ROUTE",
          "path": "/dict",
          "name": "字典",
          "route": "dict/index",
          "parent": "setting",
          "permissions": ["dict:read"]
        },
        {
          "type": "EXTERNAL_LINK",
          "path": "https://example.domain/telemetry",
          "name": "遥测"
        },
        {
          "type": "EMBEDDED",
          "path": "/schedule",
          "name": "定时任务",
          "route": "https://example.domain/schedule"
        }
      ]
      ```

通过实现这些接口，服务可以将其自身的能力和配置以标准化的方式暴露出去，从而被中心化的管理系统（如权限管理模块、API 网关等）动态地发现、集成和管理。
