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

package io.github.xezzon.zeroweb.locale.internal;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.metadata.PermissionConstant;
import io.github.xezzon.zeroweb.locale.Language;
import io.github.xezzon.zeroweb.locale.entity.AddLanguageReq;
import io.github.xezzon.zeroweb.locale.entity.ModifyLanguageReq;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// 语言管理
///
/// @author xezzon
@RestController
@RequestMapping("/language")
public class LanguageHttpEndpoint {

  private final LocalizedService localizedService;

  LanguageHttpEndpoint(final LocalizedService localizedService) {
    this.localizedService = localizedService;
  }

  /// 新增语言。
  ///
  /// @param req 语言请求体。
  /// @return 新增语言的 ID。
  @SaCheckPermission({PermissionConstant.LOCALE_WRITE})
  @PostMapping()
  public Id addLanguage(@RequestBody final AddLanguageReq req) {
    final Language language = req.into();
    localizedService.addLanguage(language);
    return Id.of(language.getId());
  }

  /// 查询语言列表。
  ///
  /// @return 语言列表。
  @GetMapping()
  public List<Language> queryLanguageList() {
    return localizedService.queryLanguageList();
  }

  /// 更新语言。
  ///
  /// @param req 语言请求体。
  @SaCheckPermission({PermissionConstant.LOCALE_WRITE})
  @PutMapping()
  public void updateLanguage(@RequestBody final ModifyLanguageReq req) {
    final Language language = req.into();
    localizedService.updateLanguage(language);
  }

  /// 删除语言。
  ///
  /// @param id 语言 ID。
  @SaCheckPermission({PermissionConstant.LOCALE_WRITE})
  @DeleteMapping("/{id}")
  public void deleteLanguage(@PathVariable final String id) {
    localizedService.deleteLanguage(id);
  }
}
