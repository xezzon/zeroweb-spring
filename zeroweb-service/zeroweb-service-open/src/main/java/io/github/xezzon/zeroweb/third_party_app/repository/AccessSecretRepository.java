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

package io.github.xezzon.zeroweb.third_party_app.repository;

import io.github.xezzon.zeroweb.third_party_app.AccessSecret;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/// 访问凭据数据访问层
///
/// 提供对第三方应用访问凭据的数据库操作
///
/// @author xezzon
@Repository
@NullMarked
public interface AccessSecretRepository extends JpaRepository<AccessSecret, String> {

  /// 根据ID更新密钥
  ///
  /// @param id 应用ID
  /// @param secretKey 新的密钥
  /// @return 更新影响的行数
  @Transactional
  @Modifying
  @Query("update AccessSecret a set a.secretKey = :secretKey where a.id = :id")
  int updateSecretKeyById(@Param("id") String id, @Param("secretKey") String secretKey);
}
