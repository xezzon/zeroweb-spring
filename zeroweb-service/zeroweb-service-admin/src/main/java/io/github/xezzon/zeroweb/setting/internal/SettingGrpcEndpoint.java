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

package io.github.xezzon.zeroweb.setting.internal;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Struct;
import com.google.protobuf.Struct.Builder;
import com.google.protobuf.util.JsonFormat;
import io.github.xezzon.zeroweb.common.exception.ZerowebRuntimeException;
import io.github.xezzon.zeroweb.setting.GetSettingRequest;
import io.github.xezzon.zeroweb.setting.Setting;
import io.github.xezzon.zeroweb.setting.SettingItem;
import io.github.xezzon.zeroweb.setting.SettingServiceGrpc.SettingServiceImplBase;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Resource;
import org.springframework.grpc.server.service.GrpcService;
import tools.jackson.databind.ObjectMapper;

/// 业务参数gRPC服务访问点
///
/// 提供gRPC协议的远程调用接口，用于其他服务查询业务参数配置。
/// 支持通过参数标识获取参数值，支持JSON格式的数据转换。
/// @author xezzon
@GrpcService
public class SettingGrpcEndpoint extends SettingServiceImplBase {

  /// 业务参数服务，用于执行参数查询业务逻辑
  private final SettingService settingService;
  /// JSON对象映射器，用于数据格式转换
  @Resource
  private ObjectMapper objectMapper;

  /// 依赖注入
  ///
  /// @param settingService 业务参数服务实例
  public SettingGrpcEndpoint(final SettingService settingService) {
    this.settingService = settingService;
  }

  /// 查询业务参数
  ///
  /// 根据参数标识查询对应的参数值，返回JSON格式的结构化数据。
  /// 支持内部服务的远程调用，提供高效的参数查询服务。
  /// @param request 查询请求，包含要查询的参数标识
  /// @param responseObserver 响应观察器，用于返回查询结果
  /// @throws ZerowebRuntimeException 当数据格式转换失败时抛出
  @Override
  public void getSetting(
      final GetSettingRequest request,
      final StreamObserver<SettingItem> responseObserver
  ) {
    try {
      // 查询参数配置
      Setting setting = settingService.queryByCode(request.getCode());
      // 将Java Map转换为Protobuf Struct格式
      String valueJson = objectMapper.writeValueAsString(setting.getValue());
      Builder valueBuilder = Struct.newBuilder();
      JsonFormat.parser().merge(valueJson, valueBuilder);
      // 返回结构化的参数项
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
