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

package io.github.xezzon.zeroweb.locale.repository;

import io.github.xezzon.zeroweb.locale.II18nMessage;
import io.github.xezzon.zeroweb.locale.Translation;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/// 对国际化文本进行数据库操作的 JPA 实体
/// @author xezzon
@Repository
@NullMarked
public interface TranslationRepository extends
    JpaRepository<Translation, String>,
    JpaSpecificationExecutor<Translation> {


  /// 根据命名空间和消息键查找国际化翻译文本列表。
  ///
  /// @param namespace 国际化内容命名空间。
  /// @param messageKey 国际化内容消息键。
  /// @return 国际化翻译文本列表。
  List<Translation> findByNamespaceAndMessageKey(String namespace, String messageKey);

  /// 根据命名空间、消息键和语言查找国际化翻译文本。
  ///
  /// @param namespace 国际化内容命名空间。
  /// @param messageKey 国际化内容消息键。
  /// @param language 语言。
  /// @return 包含国际化翻译文本的 Optional 对象。
  Optional<Translation> findByNamespaceAndMessageKeyAndLanguage(
      String namespace, String messageKey, String language
  );

  /// 根据命名空间和语言查找国际化翻译文本列表。
  ///
  /// @param namespace 国际化内容命名空间。
  /// @param language 语言。
  /// @return 国际化翻译文本列表。
  List<Translation> findByNamespaceAndLanguage(String namespace, String language);

  /// 根据旧的国际化内容更新翻译文本的命名空间和消息键。
  ///
  /// @param oldI18nMessage 旧的国际化内容。
  /// @param newI18nMessage 新的国际化内容。
  /// @return 更新的记录数。
  @Transactional
  @Modifying
  @Query("""
      update Translation i
      set i.namespace = :#{#n.namespace},
      i.messageKey = :#{#n.messageKey}
      where i.namespace = :#{#o.namespace}
      and i.messageKey = :#{#o.messageKey}"""
  )
  int updateByNamespaceAndMessageKey(
      @Param("o") II18nMessage oldI18nMessage,
      @Param("n") II18nMessage newI18nMessage
  );


  /// 根据命名空间和消息键删除国际化翻译文本。
  ///
  /// @param namespace 国际化内容命名空间。
  /// @param messageKey 国际化内容消息键。
  @Transactional
  void deleteByNamespaceAndMessageKey(String namespace, String messageKey);

  /// 根据语言删除国际化翻译文本。
  ///
  /// @param language 语言。
  @Transactional
  void deleteByLanguage(String language);

  /// 根据旧的语言标签更新翻译文本的语言标签。
  ///
  /// @param oldLanguageTag 旧的语言标签。
  /// @param newLanguageTag 新的语言标签。
  /// @return 更新的记录数。
  @Transactional
  @Modifying
  @Query("update Translation i set i.language = ?2 where i.language = ?1")
  int updateByLanguage(String oldLanguageTag, String newLanguageTag);
}
