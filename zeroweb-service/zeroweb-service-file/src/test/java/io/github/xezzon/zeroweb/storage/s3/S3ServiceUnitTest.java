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

package io.github.xezzon.zeroweb.storage.s3;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.storage.UploadEndpoint;
import io.github.xezzon.zeroweb.storage.s3.entity.S3UploadId;
import io.github.xezzon.zeroweb.storage.s3.repository.S3UploadIdRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListPartsRequest;
import software.amazon.awssdk.services.s3.model.ListPartsResponse;
import software.amazon.awssdk.services.s3.model.Part;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/// [S3Service] 分段上传地址逻辑的单元测试。
///
/// @author xezzon
class S3ServiceUnitTest {

  private S3Client s3Client;
  private S3Presigner s3Presigner;
  private S3Service s3Service;
  private Attachment attachment;

  @BeforeEach
  void setUp() {
    ZerowebS3Config config = mock(ZerowebS3Config.class);
    when(config.getBucket()).thenReturn("test");
    s3Client = mock(S3Client.class);
    s3Presigner = mock(S3Presigner.class);
    S3UploadIdRepository repository = mock(S3UploadIdRepository.class);
    s3Service = new S3Service(config, s3Presigner, s3Client, repository);

    attachment = new Attachment();
    attachment.setId("attachment-id");
    attachment.setName("test.txt");
    attachment.setType("text/plain");
    attachment.setCreateTime(Instant.parse("2026-01-02T03:04:05Z"));
    when(repository.findById("attachment-id")).thenReturn(Optional.of(
        new S3UploadId("attachment-id", "upload-id", "crc")
    ));
  }

  @Test
  void shouldSkipPresigningWhenPartAlreadyUploaded() {
    when(s3Client.listParts(
        ArgumentMatchers.<Consumer<ListPartsRequest.Builder>>any()
    )).thenReturn(ListPartsResponse.builder()
        .parts(Part.builder().partNumber(1).eTag("etag").build())
        .build());

    UploadEndpoint endpoint = s3Service.getUploadAddress(attachment, 1, "crc");

    Assertions.assertEquals(1, endpoint.getPartNumber());
    Assertions.assertNull(endpoint.getEndpoint());
    verifyNoInteractions(s3Presigner);
  }
}
