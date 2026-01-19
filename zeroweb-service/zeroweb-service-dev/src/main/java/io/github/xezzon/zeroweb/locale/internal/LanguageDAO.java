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

package io.github.xezzon.zeroweb.locale.internal;

import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import io.github.xezzon.zeroweb.locale.Language;
import io.github.xezzon.zeroweb.locale.repository.LanguageRepository;
import java.util.List;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;

/// 语言数据访问对象。
///
/// @author xezzon
@Repository
public class LanguageDAO extends BaseDAO<Language, String, LanguageRepository> {

  /// 依赖注入
  /// @param repository 语言 JPA 接口
  LanguageDAO(final LanguageRepository repository) {
    super(repository, Language.class);
  }

  /// 获取语言复制器。
  ///
  /// @return 语言复制器实例。
  @Override
  public ICopier<Language> getCopier() {
    return Copier.INSTANCE;
  }

  /// 根据语言标签查找语言。
  ///
  /// @param languageTag 语言标签。
  /// @return 包含语言的 Optional 对象。
  Optional<Language> findByLanguageTag(final String languageTag) {
    return this.get().findByDictTagAndLanguageTag(Language.LANGUAGE_DICT_TAG, languageTag);
  }

  /// 按照排序查询所有语言。
  ///
  /// @return 语言列表。
  List<Language> findAllOrderByOrdinalAsc() {
    return this.get().findByDictTagOrderByOrdinalAsc(Language.LANGUAGE_DICT_TAG);
  }

  /// 语言复制器接口。
  @Mapper
  interface Copier extends ICopier<Language> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
