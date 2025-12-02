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

package io.github.xezzon.zeroweb.setting.internal;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Struct;
import com.google.protobuf.Struct.Builder;
import com.google.protobuf.util.JsonFormat;
import io.github.xezzon.zeroweb.common.exception.ZerowebRuntimeException;
import io.github.xezzon.zeroweb.setting.GetSettingRequest;
import io.github.xezzon.zeroweb.setting.Setting;
import io.github.xezzon.zeroweb.setting.SettingGrpc.SettingImplBase;
import io.github.xezzon.zeroweb.setting.SettingItem;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Resource;
import org.springframework.grpc.server.service.GrpcService;
import tools.jackson.databind.ObjectMapper;

/// 业务参数管理
/// @author xezzon
@GrpcService
public class SettingGrpcEndpoint extends SettingImplBase {

  private final SettingService settingService;
  @Resource
  private ObjectMapper objectMapper;

  public SettingGrpcEndpoint(final SettingService settingService) {
    this.settingService = settingService;
  }

  @Override
  public void getSetting(
      final GetSettingRequest request,
      final StreamObserver<SettingItem> responseObserver
  ) {
    try {
      Setting setting = settingService.queryByCode(request.getCode());
      String valueJson = objectMapper.writeValueAsString(setting.getValue());
      Builder valueBuilder = Struct.newBuilder();
      JsonFormat.parser().merge(valueJson, valueBuilder);
      responseObserver.onNext(SettingItem.newBuilder()
          .setCode(setting.getCode())
          .setValue(valueBuilder.build())
          .build()
      );
    } catch (InvalidProtocolBufferException e) {
      throw new ZerowebRuntimeException(e);
    } finally {
      responseObserver.onCompleted();
    }
  }
}
