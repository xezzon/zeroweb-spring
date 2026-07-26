package io.github.xezzon.zeroweb.common.grpc;

import static io.github.xezzon.zeroweb.common.grpc.GrpcServerExceptionHandler.ERROR_CODE;

import io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant;
import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.setting.GetSettingRequest;
import io.github.xezzon.zeroweb.setting.SettingServiceGrpc.SettingServiceBlockingStub;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureTestGrpcTransport;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@AutoConfigureTestGrpcTransport
class GrpcServerExceptionHandlerTest {

  @Resource
  private SettingServiceBlockingStub settingBlockingStub;

  @Test
  void businessException() {
    String errorCode = RepeatDataException.ERROR_CODE;
    GetSettingRequest request = GetSettingRequest.newBuilder()
        .setCode(errorCode)
        .build();
    StatusRuntimeException exception = Assertions.assertThrows(StatusRuntimeException.class, () ->
        settingBlockingStub.getSetting(request)
    );
    Assertions.assertEquals(Status.INVALID_ARGUMENT, exception.getStatus());
    Assertions.assertNotNull(exception.getTrailers());
    Assertions.assertEquals(errorCode, exception.getTrailers().get(ERROR_CODE));
  }

  @Test
  void unauthenticated() {
    String errorCode = ErrorCodeConstant.UNAUTHENTICATED;
    GetSettingRequest request = GetSettingRequest.newBuilder()
        .setCode(errorCode)
        .build();
    StatusRuntimeException exception = Assertions.assertThrows(StatusRuntimeException.class, () ->
        settingBlockingStub.getSetting(request)
    );
    Assertions.assertEquals(Status.UNAUTHENTICATED, exception.getStatus());
    Assertions.assertNotNull(exception.getTrailers());
    Assertions.assertEquals(errorCode, exception.getTrailers().get(ERROR_CODE));
  }

  @Test
  void unauthorized() {
    String errorCode = ErrorCodeConstant.UNAUTHORIZED;
    GetSettingRequest request = GetSettingRequest.newBuilder()
        .setCode(errorCode)
        .build();
    StatusRuntimeException exception = Assertions.assertThrows(StatusRuntimeException.class, () ->
        settingBlockingStub.getSetting(request)
    );
    Assertions.assertEquals(Status.PERMISSION_DENIED, exception.getStatus());
    Assertions.assertNotNull(exception.getTrailers());
    Assertions.assertEquals(errorCode, exception.getTrailers().get(ERROR_CODE));
  }

  @Test
  void noSuchData() {
    String errorCode = ErrorCodeConstant.NO_SUCH_DATA;
    GetSettingRequest request = GetSettingRequest.newBuilder()
        .setCode(errorCode)
        .build();
    StatusRuntimeException exception = Assertions.assertThrows(StatusRuntimeException.class, () ->
        settingBlockingStub.getSetting(request)
    );
    Assertions.assertEquals(Status.INVALID_ARGUMENT, exception.getStatus());
    Assertions.assertNotNull(exception.getTrailers());
    Assertions.assertEquals(errorCode, exception.getTrailers().get(ERROR_CODE));
  }

  @Test
  void unknown() {
    String errorCode = ErrorCodeConstant.UNKNOWN;
    GetSettingRequest request = GetSettingRequest.newBuilder()
        .setCode(errorCode)
        .build();
    StatusRuntimeException exception = Assertions.assertThrows(StatusRuntimeException.class, () ->
        settingBlockingStub.getSetting(request)
    );
    Assertions.assertEquals(Status.UNKNOWN, exception.getStatus());
    Assertions.assertNotNull(exception.getTrailers());
    Assertions.assertEquals(errorCode, exception.getTrailers().get(ERROR_CODE));
  }
}
