package io.github.xezzon.zeroweb.common.grpc;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant;
import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.setting.GetSettingRequest;
import io.github.xezzon.zeroweb.setting.SettingGrpc.SettingImplBase;
import io.github.xezzon.zeroweb.setting.SettingItem;
import io.grpc.stub.StreamObserver;
import java.util.NoSuchElementException;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class TestUserGrpcServer extends SettingImplBase {

  @Override
  public void getSetting(
      final GetSettingRequest request,
      final StreamObserver<SettingItem> responseObserver
  ) {
    throw switch (request.getCode()) {
      case RepeatDataException.ERROR_CODE -> new RepeatDataException(request.getCode());
      case ErrorCodeConstant.UNAUTHENTICATED -> new NotLoginException("", "", "");
      case ErrorCodeConstant.UNAUTHORIZED -> new NotRoleException("");
      case ErrorCodeConstant.NO_SUCH_DATA -> new NoSuchElementException();
      default -> new RuntimeException();
    };
  }
}
