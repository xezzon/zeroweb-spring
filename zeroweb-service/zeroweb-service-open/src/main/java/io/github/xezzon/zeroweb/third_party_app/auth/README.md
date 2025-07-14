# 第三方应用认证授权模块设计文档

本模块负责处理第三方应用的认证和授权相关逻辑，包括角色管理、成员管理和权限控制。

## 主要组件

- `ThirdPartyAppAuthService`: 核心服务类，封装了所有的认证授权操作。
- `ThirdPartyAppRole`: 角色实体，定义了用户组内的角色。
- `ThirdPartyAppMember`: 成员实体，表示用户与角色的关联关系。
- `ThirdPartyAppRoleRepository`: `ThirdPartyAppRole` 的 JPA 仓库。
- `ThirdPartyAppMemberRepository`: `ThirdPartyAppMember` 的 JPA 仓库。

## `ThirdPartyAppAuthService` 核心方法

- `addGroupRole(ThirdPartyAppRole role)`: 添加一个新的用户组角色。
- `listGroupRole(String groupId)`: 列出指定用户组的所有角色。
- `deleteGroupRole(String roleId)`: 删除一个角色，并解绑所有关联的成员。
- `addGroupMember(ThirdPartyAppMember member)`: 确认待分配人员的角色，或为现有成员添加新的角色。
- `listGroupMemberWithRole(String roleId)`: 列出拥有指定角色的所有成员。该方法将返回一个 `ThirdPartyAppMember` 列表。
- `releaseMember(ThirdPartyAppMember member)`: 解绑用户组角色与成员。
- `removeMember(String groupId, String userId)`: 从用户组中移除一个成员。
- `listAllPermission()`: 列出当前用户在所有用户组中拥有的全部资源权限。
- `checkPermission(String userId, String permission)`: 校验指定用户是否拥有特定权限。如果用户不具备该权限，将抛出异常。