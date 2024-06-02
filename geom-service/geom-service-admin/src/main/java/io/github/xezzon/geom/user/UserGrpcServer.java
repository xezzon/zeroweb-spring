package io.github.xezzon.geom.user;

import cn.hutool.crypto.digest.BCrypt;
import io.github.xezzon.geom.user.converter.AddUserReqConverter;
import io.github.xezzon.geom.user.domain.User;
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
