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

package io.github.xezzon.zeroweb.common.grpc;

import io.github.xezzon.zeroweb.attachment.AttachmentGrpc.AttachmentBlockingStub;
import io.github.xezzon.zeroweb.attachment.AttachmentGrpc.AttachmentStub;
import io.github.xezzon.zeroweb.dict.DictGrpc.DictBlockingStub;
import io.github.xezzon.zeroweb.dict.DictGrpc.DictStub;
import io.github.xezzon.zeroweb.setting.SettingGrpc.SettingBlockingStub;
import io.github.xezzon.zeroweb.user.UserGrpc.UserBlockingStub;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.grpc.client.GrpcChannelBuilderCustomizer;
import org.springframework.grpc.client.ImportGrpcClients;
import org.springframework.stereotype.Component;

/**
 * 构造 gRPC 客户端 Bean。
 * @author xezzon
 */
@ImportGrpcClients(
    prefix = "default",
    target = "admin",
    types = {
        DictBlockingStub.class,
        DictStub.class,
        UserBlockingStub.class,
        SettingBlockingStub.class
    }
)
@ImportGrpcClients(
    prefix = "default",
    target = "file",
    types = {
        AttachmentBlockingStub.class,
        AttachmentStub.class
    }
)
@Component
public class GrpcClientFactory {

  /// gRPC 全局配置
  @Bean
  @Order(200)
  <T extends ManagedChannelBuilder<T>> GrpcChannelBuilderCustomizer<T> retryChannelCustomizer() {
    return (_, builder) -> builder.enableRetry().maxRetryAttempts(3);
  }
}
