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

package io.github.xezzon.zeroweb.metadata;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// 服务自省
///
/// @author xezzon
@RestController
@RequestMapping("/metadata")
public class MetadataHttpEndpoint {

  /// 当前应用的名称，从 Spring Environment 中注入。
  @Value("${spring.application.name}")
  private String appName;
  /// 当前应用的版本，从 Spring Environment 中注入。
  @Value("${spring.application.version}")
  private String appVersion;
  /// 注入所有实现 [IMenuService] 接口的服务，用于收集菜单/资源信息。
  @Resource
  private List<IMenuService> resourceServices;

  /// 获取当前服务的自省信息。
  ///
  /// @return 包含服务名称、版本、类型等信息的 [ServiceInfo] 对象。
  @GetMapping("/info.json")
  public ServiceInfo loadServiceInfo() {
    final ServiceInfo serviceInfo = new ServiceInfo();
    serviceInfo.setName(appName);
    serviceInfo.setVersion(appVersion);
    serviceInfo.setType(ServiceType.SERVER);
    serviceInfo.setHidden(true);
    return serviceInfo;
  }

  /// 获取当前服务提供的所有菜单或资源信息。
  ///
  /// @return 包含所有 [MenuInfo] 对象的列表，聚合了所有 [IMenuService] 的结果。
  @GetMapping("/menu.json")
  public List<MenuInfo> loadResourceInfo() {
    return resourceServices.stream()
        .map(IMenuService::list)
        .flatMap(Collection::stream)
        .toList();
  }
}
