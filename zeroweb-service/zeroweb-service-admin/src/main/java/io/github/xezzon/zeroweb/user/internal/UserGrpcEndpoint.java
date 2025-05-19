package io.github.xezzon.zeroweb.user.internal;

import cn.dev33.satoken.secure.BCrypt;
import io.github.xezzon.zeroweb.user.AddUserReq;
import io.github.xezzon.zeroweb.user.AddUserResp;
import io.github.xezzon.zeroweb.user.UserGrpc.UserImplBase;
import io.github.xezzon.zeroweb.user.converter.AddUserReqConverter;
import io.github.xezzon.zeroweb.user.User;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

/**
 * 用户功能Grpc接口
 * @author xezzon
 */
@GrpcService
public class UserGrpcEndpoint extends UserImplBase {

  private final UserService userService;

  UserGrpcEndpoint(final UserService userService) {
    this.userService = userService;
  }

  /**
   * 新增用户（服务间接口）
   */
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
