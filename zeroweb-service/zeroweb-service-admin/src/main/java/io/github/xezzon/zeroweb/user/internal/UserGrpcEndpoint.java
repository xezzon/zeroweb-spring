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

package io.github.xezzon.zeroweb.user.internal;

import cn.dev33.satoken.secure.BCrypt;
import io.github.xezzon.zeroweb.user.AddUserReq;
import io.github.xezzon.zeroweb.user.AddUserResp;
import io.github.xezzon.zeroweb.user.User;
import io.github.xezzon.zeroweb.user.UserGrpc.UserImplBase;
import io.github.xezzon.zeroweb.user.converter.AddUserReqConverter;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

/// 用户功能Grpc接口
///
/// @author xezzon
@GrpcService
public class UserGrpcEndpoint extends UserImplBase {

  private final UserService userService;

  UserGrpcEndpoint(final UserService userService) {
    this.userService = userService;
  }

  /// 新增用户（服务间接口）
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
}
