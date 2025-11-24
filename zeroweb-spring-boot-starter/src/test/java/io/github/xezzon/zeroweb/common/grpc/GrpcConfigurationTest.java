package io.github.xezzon.zeroweb.common.grpc;

import io.github.xezzon.zeroweb.auth.JwtAuth;
import io.github.xezzon.zeroweb.auth.TestJwtGenerator;
import io.github.xezzon.zeroweb.dict.DictGrpc.DictBlockingStub;
import io.github.xezzon.zeroweb.dict.DictImportReqList;
import io.github.xezzon.zeroweb.dict.DictReq;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureInProcessTransport;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xezzon
 */
@SpringBootTest
@AutoConfigureInProcessTransport
class GrpcConfigurationTest {

  @Resource
  private DictBlockingStub dictBlockingStub;

  @Test
  void jwtInterceptor() {
    final TestJwtGenerator.Builder jwtBuilder = TestJwtGenerator.userBuilder().username("test");
    Assertions.assertDoesNotThrow(() ->
        ScopedValue.where(JwtAuth.CLAIM, jwtBuilder.jwtClaim())
            .call(() -> dictBlockingStub.getDictListByTag(DictReq.newBuilder().build()))
    );
  }

  @Test
  void jwtInterceptor_noJwt() {
    Assertions.assertDoesNotThrow(() ->
        dictBlockingStub.importDict(DictImportReqList.newBuilder().build())
    );
  }
}
