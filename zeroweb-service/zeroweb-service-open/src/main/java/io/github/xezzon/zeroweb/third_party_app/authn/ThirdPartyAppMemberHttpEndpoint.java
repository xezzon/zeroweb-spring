/*
 * SPDX-FileCopyrightText: Copyright (C) 2025-2026 xezzon
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This file is part of ZeroWeb.
 *
 * ZeroWeb is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * ZeroWeb is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with ZeroWeb. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.xezzon.zeroweb.third_party_app.authn;

import io.github.xezzon.zeroweb.auth.JwtAuth;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.third_party_app.authz.ThirdPartyAppPermissionConstant;
import io.github.xezzon.zeroweb.third_party_app.authz.ThirdPartyAppPermissionManager;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/// 第三方应用成员管理
/// @author xezzon
@RestController
public class ThirdPartyAppMemberHttpEndpoint {

  private final ThirdPartyAppMemberService thirdPartyAppMemberService;
  private final ThirdPartyAppPermissionManager thirdPartyAppPermissionManager;

  /// 依赖注入
  /// @param thirdPartyAppMemberService 第三方应用成员管理服务
  /// @param thirdPartyAppPermissionManager 第三方应用权限管理服务
  public ThirdPartyAppMemberHttpEndpoint(
      ThirdPartyAppMemberService thirdPartyAppMemberService,
      ThirdPartyAppPermissionManager thirdPartyAppPermissionManager
  ) {
    this.thirdPartyAppMemberService = thirdPartyAppMemberService;
    this.thirdPartyAppPermissionManager = thirdPartyAppPermissionManager;
  }

  /// 邀请人员成为应用的成员。
  ///
  /// @param appId 应用ID
  /// @param userId 用户ID。如果为空，则生成对所有人有效的邀请码
  /// @param timeout 邀请码有效期。单位：小时。默认为24。
  /// @return 邀请码
  @PostMapping("/third-party-app/{appId}/member")
  public String inviteMember(
      @PathVariable @NotBlank final String appId,
      @RequestParam(required = false) final String userId,
      @RequestParam(required = false, defaultValue = "24") final int timeout
  ) {
    thirdPartyAppPermissionManager
        .check(appId, JwtAuth.getOrThrow().getSub(), ThirdPartyAppPermissionConstant.INVITE_MEMBER);
    return thirdPartyAppMemberService.inviteMember(appId, userId, timeout);
  }

  /// 持邀请码加入应用
  ///
  /// @param token 邀请码
  /// @return 成员ID
  @PutMapping("/third-party-app/-/member")
  public Id acceptInvitation(@RequestParam @NotBlank final String token) {
    String id = thirdPartyAppMemberService.acceptInvitation(token);
    return Id.of(id);
  }

  /// 查询第三方应用的成员
  ///
  /// @param appId 第三方应用ID
  /// @return 第三方应用成员列表
  @GetMapping("/third-party-app/{appId}/member")
  public List<ThirdPartyAppMember> listMember(@PathVariable @NotBlank final String appId) {
    thirdPartyAppPermissionManager
        .check(appId, JwtAuth.getOrThrow().getSub(), ThirdPartyAppPermissionConstant.LIST_MEMBER);
    return thirdPartyAppMemberService.listMember(appId);
  }

  /// 第三方应用所有权转移
  ///
  /// @param appId 第三方应用 ID
  /// @param userId 转移的目标用户
  @PatchMapping("/third-party-app/{appId}/owner")
  public void moveOwnership(
      @PathVariable @NotBlank final String appId,
      @RequestParam @NotBlank final String userId
  ) {
    thirdPartyAppMemberService.moveOwnership(appId, userId);
  }

  /// 将成员从第三方应用的成员中移除
  /// @param appId 应用 ID
  /// @param id 成员 ID
  @DeleteMapping("/third-party-app/{appId}/member/{id}")
  public void deleteMember(
      @PathVariable final String appId,
      @PathVariable final String id
  ) {
    thirdPartyAppPermissionManager
        .check(appId, JwtAuth.getOrThrow().getSub(), ThirdPartyAppPermissionConstant.DELETE_MEMBER);
    thirdPartyAppMemberService.deleteMember(appId, id);
  }
}
