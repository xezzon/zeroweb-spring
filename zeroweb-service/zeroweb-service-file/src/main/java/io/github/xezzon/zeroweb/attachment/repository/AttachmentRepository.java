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

package io.github.xezzon.zeroweb.attachment.repository;

import io.github.xezzon.zeroweb.attachment.Attachment;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/// 对 [附件][Attachment] 进行数据库操作的 JPA 接口
/// @author xezzon
@Repository
@NullMarked
public interface AttachmentRepository extends
    JpaRepository<Attachment, String>,
    JpaSpecificationExecutor<Attachment> {

  /**
   * 查询业务关联的附件列表
   * @param bizType 业务类型
   * @param bizId 业务ID
   * @return 附件列表
   */
  List<Attachment> findByBizTypeAndBizId(String bizType, String bizId);
}
