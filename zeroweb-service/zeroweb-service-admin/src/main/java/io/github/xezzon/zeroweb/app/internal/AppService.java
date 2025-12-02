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

package io.github.xezzon.zeroweb.app.internal;

import io.github.xezzon.zeroweb.app.App;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/// @author xezzon
@Service
public class AppService {

  private final AppDAO appDAO;

  public AppService(final AppDAO appDAO) {
    this.appDAO = appDAO;
  }

  /// 新增服务
  ///
  /// @param app 服务信息
  void addApp(final App app) {
    appDAO.get().save(app);
  }

  /// 查询服务列表
  ///
  /// @return 服务列表
  List<App> listApp() {
    return appDAO.get().findAllByOrderByOrdinalAsc();
  }

  /// 更新服务信息
  ///
  /// @param app 服务信息
  void updateApp(final App app) {
    final App entity = appDAO.get().findById(app.getId())
        .orElseThrow(EntityNotFoundException::new);
    final App oldValue = new App();
    appDAO.getCopier().copy(entity, oldValue);
    appDAO.get().save(app);
  }

  /// 删除服务
  ///
  /// @param id 服务ID
  void deleteApp(final String id) {
    final Optional<App> app = appDAO.get().findById(id);
    if (app.isEmpty()) {
      return;
    }
    appDAO.get().deleteById(id);
  }
}
