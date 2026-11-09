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

package io.github.xezzon.zeroweb.attachment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/// [Attachment#partCount(long)] 的单元测试。
///
/// @author xezzon
class AttachmentTest {

  private static final long PART_SIZE = 10L;

  @Test
  void shouldReturnOnePartWhenSizeIsZero() {
    Attachment attachment = new Attachment();
    attachment.setSize(0L);

    Assertions.assertEquals(1, attachment.partCount(PART_SIZE));
  }

  @Test
  void shouldReturnOnePartWhenSizeEqualsPartSize() {
    Attachment attachment = new Attachment();
    attachment.setSize(PART_SIZE);

    Assertions.assertEquals(1, attachment.partCount(PART_SIZE));
  }

  @Test
  void shouldReturnTwoPartsWhenSizeExceedsPartSizeByOne() {
    Attachment attachment = new Attachment();
    attachment.setSize(PART_SIZE + 1);

    Assertions.assertEquals(2, attachment.partCount(PART_SIZE));
  }
}
