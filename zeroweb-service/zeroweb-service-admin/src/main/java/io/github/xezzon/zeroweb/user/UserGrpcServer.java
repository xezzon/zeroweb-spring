package io.github.xezzon.zeroweb.user;

import cn.dev33.satoken.secure.BCrypt;
import io.github.xezzon.zeroweb.user.converter.AddUserReqConverter;
import io.github.xezzon.zeroweb.user.domain.User;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * 用户功能Grpc接口
 * @author xezzon
 */
@GrpcService
public class UserGrpcServer extends UserGrpc.UserImplBase {

  private final UserService userService;

  UserGrpcServer(UserService userService) {
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
