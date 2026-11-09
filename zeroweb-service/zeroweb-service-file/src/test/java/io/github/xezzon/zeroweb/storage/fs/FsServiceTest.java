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

package io.github.xezzon.zeroweb.storage.fs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.hash.Hashing;
import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.IAttachmentService;
import io.github.xezzon.zeroweb.attachment.event.AttachmentUploadedEvent;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.common.constant.BannerConstant;
import io.github.xezzon.zeroweb.common.exception.IncorrectFileException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileSystemUtils;

/// [FsService] 分段合并与校验逻辑的单元测试。
///
/// @author xezzon
class FsServiceTest {

  private static final long PART_SIZE = 4L;
  private static final byte[] CONTENT = "01234567".getBytes(StandardCharsets.UTF_8);
  private static final Path TEMP_DIR = Path.of(System.getProperty("java.io.tmpdir"))
      .resolve(BannerConstant.NAME);

  private Path basePath;
  private Path tempAttachmentDir;
  private FsService fsService;
  private Attachment attachment;

  @BeforeEach
  void setUp() throws IOException {
    basePath = Files.createTempDirectory("zeroweb-fs-test-");
    attachment = new Attachment();
    attachment.setId(UUID.randomUUID().toString());
    attachment.setName("test.txt");
    attachment.setType("text/plain");
    attachment.setProvider(FileProviderEnum.FS);
    attachment.setCreateTime(Instant.parse("2026-01-02T03:04:05Z"));
    attachment.setChecksum(checksum(CONTENT));
    attachment.setSize((long) CONTENT.length);
    tempAttachmentDir = TEMP_DIR.resolve(attachment.getId());

    ZerowebFsConfig config = mock(ZerowebFsConfig.class);
    when(config.getBasePath()).thenReturn(basePath);
    when(config.getPartSize()).thenReturn(PART_SIZE);
    fsService = new FsService(config, mock(IAttachmentService.class));
  }

  @AfterEach
  void tearDown() throws IOException {
    FileSystemUtils.deleteRecursively(basePath);
    FileSystemUtils.deleteRecursively(tempAttachmentDir);
  }

  @Test
  void shouldMergeOutOfOrderPartsWhenSizeAndChecksumMatch() throws IOException {
    writePart(2, Arrays.copyOfRange(CONTENT, 4, CONTENT.length));
    writePart(1, Arrays.copyOfRange(CONTENT, 0, 4));

    fsService.listen(new AttachmentUploadedEvent(attachment));

    Path storedFile = basePath.resolve(attachment.objectKey());
    Assertions.assertArrayEquals(CONTENT, Files.readAllBytes(storedFile));
    Assertions.assertFalse(Files.exists(tempAttachmentDir));
  }

  @Test
  void shouldRejectMergedFileWhenSizeMismatch() throws IOException {
    attachment.setSize((long) CONTENT.length + 1);
    writePart(1, Arrays.copyOfRange(CONTENT, 0, 4));
    writePart(2, Arrays.copyOfRange(CONTENT, 4, CONTENT.length));

    Assertions.assertThrows(
        IncorrectFileException.class,
        () -> fsService.listen(new AttachmentUploadedEvent(attachment))
    );
    Assertions.assertFalse(Files.exists(basePath.resolve(attachment.objectKey())));
    Assertions.assertFalse(Files.exists(tempAttachmentDir));
  }

  @Test
  void shouldRejectMergedFileWhenChecksumMismatch() throws IOException {
    attachment.setChecksum(checksum("mismatch".getBytes(StandardCharsets.UTF_8)));
    writePart(1, Arrays.copyOfRange(CONTENT, 0, 4));
    writePart(2, Arrays.copyOfRange(CONTENT, 4, CONTENT.length));

    Assertions.assertThrows(
        IncorrectFileException.class,
        () -> fsService.listen(new AttachmentUploadedEvent(attachment))
    );
    Assertions.assertFalse(Files.exists(basePath.resolve(attachment.objectKey())));
    Assertions.assertFalse(Files.exists(tempAttachmentDir));
  }

  private void writePart(int partNumber, byte[] content) throws IOException {
    Files.createDirectories(tempAttachmentDir);
    Files.write(tempAttachmentDir.resolve(String.valueOf(partNumber)), content);
  }

  private static String checksum(byte[] content) {
    return Base64.getEncoder().encodeToString(Hashing.sha256().hashBytes(content).asBytes());
  }
}
