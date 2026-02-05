/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 xezzon
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

package io.github.xezzon.zeroweb.attachment.internal;

import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.repository.AttachmentRepository;
import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public class AttachmentDAO extends BaseDAO<Attachment, String, AttachmentRepository> {

  /// 依赖注入
  /// @param repository 附件 JPA 接口
  protected AttachmentDAO(final AttachmentRepository repository) {
    super(repository, Attachment.class);
  }
}
