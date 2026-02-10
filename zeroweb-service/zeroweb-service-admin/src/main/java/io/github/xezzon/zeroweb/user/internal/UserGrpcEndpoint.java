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

package io.github.xezzon.zeroweb.user.internal;

import cn.dev33.satoken.secure.BCrypt;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.github.xezzon.zeroweb.user.AddUserReq;
import io.github.xezzon.zeroweb.user.AddUserResp;
import io.github.xezzon.zeroweb.user.User;
import io.github.xezzon.zeroweb.user.UserCreatedEvent;
import io.github.xezzon.zeroweb.user.UserGrpc.UserImplBase;
import io.github.xezzon.zeroweb.user.converter.AddUserReqConverter;
import io.github.xezzon.zeroweb.user.event.UserAddEvent;
import io.grpc.stub.StreamObserver;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.context.event.EventListener;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.scheduling.annotation.Async;

/// 用户功能Grpc接口
///
/// @author xezzon
@GrpcService
public class UserGrpcEndpoint extends UserImplBase {

  /// 用户服务接口
  private final UserService userService;

  /// 用户创建事件观察者集合（线程安全）
  private final Set<StreamObserver<UserCreatedEvent>> userCreatedObservers =
      new CopyOnWriteArraySet<>();

  /// 依赖注入
  ///
  /// @param userService 用户服务接口
  UserGrpcEndpoint(final UserService userService) {
    this.userService = userService;
  }

  /// 新增用户（服务间接口）
  ///
  /// @param request 新增用户请求
  /// @param responseObserver 响应观察者
  @Override
  public void addUser(AddUserReq request, StreamObserver<AddUserResp> responseObserver) {
    User user = AddUserReqConverter.INSTANCE.from(request);
    user.setCipher(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
    userService.addUser(user);
    responseObserver.onNext(AddUserResp.newBuilder()
        .setId(user.getId())
        .build()
    );
    responseObserver.onCompleted();
  }

  /// 订阅用户创建事件（服务间接口）
  /// 客户端通过此方法订阅用户创建事件流
  ///
  /// @param request 空请求
  /// @param responseObserver 事件流观察者
  @Override
  public void onUserCreated(
      final Empty request,
      final StreamObserver<UserCreatedEvent> responseObserver
  ) {
    // 包装观察者以检测流结束
    StreamObserver<UserCreatedEvent> wrapped = new StreamObserver<>() {
      @Override
      public void onNext(UserCreatedEvent value) {
        responseObserver.onNext(value);
      }

      @Override
      public void onError(Throwable t) {
        userCreatedObservers.remove(this);
        responseObserver.onError(t);
      }

      @Override
      public void onCompleted() {
        userCreatedObservers.remove(this);
        responseObserver.onCompleted();
      }
    };

    // 注册包装后的观察者
    userCreatedObservers.add(wrapped);

    // 流保持打开状态，由 gRPC 框架管理连接生命周期
  }

  /// 监听用户新增事件，并广播到所有订阅的 gRPC 客户端
  ///
  /// @param event 用户新增事件（Spring 事件）
  @EventListener
  @Async
  public void listen(UserAddEvent event) {
    // 将 Spring 事件转换为 Protobuf 消息
    User user = event.getUser();
    UserCreatedEvent grpcEvent = UserCreatedEvent.newBuilder()
        .setUserId(user.getId())
        .setUsername(user.getUsername())
        .setNickname(user.getNickname() != null ? user.getNickname() : "")
        .setCreateTime(Timestamp.newBuilder()
            .setSeconds(user.getCreateTime().getEpochSecond())
            .setNanos(user.getCreateTime().getNano())
            .build()
        )
        .build();

    // 使用虚拟线程并发广播事件到所有观察者
    for (StreamObserver<UserCreatedEvent> observer : userCreatedObservers) {
      Thread.startVirtualThread(() -> {
        try {
          observer.onNext(grpcEvent);
        } catch (Exception _) {
          // 客户端断开连接时移除观察者
          userCreatedObservers.remove(observer);
        }
      });
    }
  }
}
