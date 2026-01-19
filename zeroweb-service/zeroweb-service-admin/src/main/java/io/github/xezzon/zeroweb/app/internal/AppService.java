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

package io.github.xezzon.zeroweb.app.internal;

import io.github.xezzon.zeroweb.app.App;
import io.github.xezzon.zeroweb.app.repository.AppRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/// `AppService` 是服务管理的服务层组件。
///
/// 它提供了服务的业务逻辑处理，包括新增、查询、更新和删除服务。
///
/// @author xezzon
@Service
public class AppService {

  private final AppRepository appRepository;

  /// 依赖注入
  ///
  /// @param appRepository 应用数据接口
  public AppService(final AppRepository appRepository) {
    this.appRepository = appRepository;
  }

  /// 新增一个服务。
  ///
  /// @param app 包含服务信息 [App] 的实体。
  void addApp(final App app) {
    appRepository.save(app);
  }

  /// 查询所有服务列表，并按顺序升序排列。
  ///
  /// @return 包含所有服务实体的列表。
  List<App> listApp() {
    return appRepository.findAllByOrderByOrdinalAsc();
  }

  /// 查询指定服务
  /// @param id 服务 ID
  /// @return 服务信息
  /// @throws java.util.NoSuchElementException ID 没有对应的服务
  App queryAppById(final String id) {
    return appRepository.findById(id)
        .orElseThrow();
  }

  /// 更新一个现有服务的信息。
  ///
  /// 如果服务不存在，将抛出 [EntityNotFoundException]。
  ///
  /// @param app 包含要更新的服务信息和其 ID 的实体。
  void updateApp(final App app) {
    appRepository.save(app);
  }

  /// 根据服务ID删除一个服务。
  ///
  /// 如果服务不存在，则不执行任何操作。
  ///
  /// @param id 要删除服务的 ID。
  void deleteApp(final String id) {
    final Optional<App> app = appRepository.findById(id);
    if (app.isEmpty()) {
      return;
    }
    appRepository.deleteById(id);
  }
}
