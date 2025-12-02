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
import io.github.xezzon.zeroweb.locale.Translation;
import io.github.xezzon.zeroweb.locale.entity.UpsertTranslationReq;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// 国际化文本管理
@RestController
@RequestMapping("/locale")
public class TranslationHttpEndpoint {

  private final LocalizedService localizedService;

  public TranslationHttpEndpoint(final LocalizedService localizedService) {
    this.localizedService = localizedService;
  }

  /// 新增/更新 国际化文本
  ///
  /// @param req 国际化文本
  @SaCheckPermission({PermissionConstant.LOCALE_WRITE})
  @PutMapping()
  public Id upsertTranslation(@RequestBody final UpsertTranslationReq req) {
    final Translation translation = req.into();
    localizedService.upsertTranslation(translation);
    return Id.of(translation.getId());
  }

  /// 加载国际化资源
  ///
  /// @param language 国际化语言
  /// @param namespace 命名空间
  /// @return 国际化内容-国际化文本
  @GetMapping("/{language}/{namespace}")
  public Map<String, String> loadTranslation(
      @PathVariable final String language,
      @PathVariable final String namespace
  ) {
    return localizedService.loadTranslation(language, namespace);
  }
}
