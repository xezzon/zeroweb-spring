/*
 * SPDX-FileCopyrightText: Copyright (C) 2025 xezzon
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

package io.github.xezzon.zeroweb.third_party_app.internal;

import static io.github.xezzon.zeroweb.third_party_app.authz.ThirdPartyAppPermissionConstant.ROLL_ACCESS_SECRET;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.xezzon.zeroweb.auth.JwtAuth;
import io.github.xezzon.zeroweb.common.metadata.PermissionConstant;
import io.github.xezzon.zeroweb.core.odata.ODataRequestParam;
import io.github.xezzon.zeroweb.third_party_app.AccessSecret;
import io.github.xezzon.zeroweb.third_party_app.ThirdPartyApp;
import io.github.xezzon.zeroweb.third_party_app.authz.ThirdPartyAppPermissionManager;
import io.github.xezzon.zeroweb.third_party_app.entity.AddThirdPartyAppReq;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// 第三方应用管理
///
/// @author xezzon
@RestController
@RequestMapping("/third-party-app")
public class ThirdPartyAppHttpEndpoint {

  private final ThirdPartyAppService thirdPartyAppService;
  private final ThirdPartyAppPermissionManager thirdPartyAppPermissionManager;

  /// 依赖注入
  /// @param thirdPartyAppService 第三方应用管理
  /// @param thirdPartyAppPermissionManager 第三方应用权限管理
  public ThirdPartyAppHttpEndpoint(
      final ThirdPartyAppService thirdPartyAppService,
      ThirdPartyAppPermissionManager thirdPartyAppPermissionManager
  ) {
    this.thirdPartyAppService = thirdPartyAppService;
    this.thirdPartyAppPermissionManager = thirdPartyAppPermissionManager;
  }

  /// 添加第三方应用
  ///
  /// @param req 请求体，包含要添加的第三方应用信息
  /// @return 添加成功后返回的第三方应用ID
  @PostMapping()
  public AccessSecret add(@RequestBody AddThirdPartyAppReq req) {
    ThirdPartyApp thirdPartyApp = req.into();
    thirdPartyApp.setOwnerId(JwtAuth.getOrThrow().getSub());
    return thirdPartyAppService.addThirdPartyApp(thirdPartyApp);
  }


  /// 获取当前用户的所有第三方应用列表
  ///
  /// @return 当前用户的所有第三方应用列表
  @GetMapping("/mine")
  public Page<@NonNull ThirdPartyApp> listMyThirdPartyApp() {
    String userId = JwtAuth.getOrThrow().getSub();
    return thirdPartyAppService.listThirdPartyAppByUser(userId);
  }

  /// 查询所有第三方应用列表
  ///
  /// @param odata 查询参数
  /// @return 所有第三方应用列表
  @GetMapping()
  @SaCheckPermission({PermissionConstant.THIRD_PARTY_APP_READ})
  public Page<@NonNull ThirdPartyApp> listThirdPartyApp(ODataRequestParam odata) {
    return thirdPartyAppService.listThirdPartyApp(odata.into());
  }

  /// 更新第三方应用的密钥
  ///
  /// @param appId 第三方应用ID
  /// @return 更新后的第三方应用的凭据与密钥
  @PatchMapping("/{appId}/roll")
  public AccessSecret rollAccessSecret(@PathVariable String appId) {
    thirdPartyAppPermissionManager.check(appId, JwtAuth.getOrThrow().getSub(), ROLL_ACCESS_SECRET);
    return thirdPartyAppService.rollAccessSecret(appId);
  }
}
