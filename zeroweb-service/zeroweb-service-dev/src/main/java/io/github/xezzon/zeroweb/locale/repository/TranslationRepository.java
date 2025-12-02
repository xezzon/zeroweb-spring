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

@Repository
@NullMarked
public interface TranslationRepository extends
    JpaRepository<Translation, String>,
    JpaSpecificationExecutor<Translation> {

  List<Translation> findByNamespaceAndMessageKey(String namespace, String messageKey);

  Optional<Translation> findByNamespaceAndMessageKeyAndLanguage(
      String namespace, String messageKey, String language
  );

  List<Translation> findByNamespaceAndLanguage(String namespace, String language);

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


  @Transactional
  void deleteByNamespaceAndMessageKey(String namespace, String messageKey);

  @Transactional
  void deleteByLanguage(String language);

  @Transactional
  @Modifying
  @Query("update Translation i set i.language = ?2 where i.language = ?1")
  int updateByLanguage(String oldLanguageTag, String newLanguageTag);
}
