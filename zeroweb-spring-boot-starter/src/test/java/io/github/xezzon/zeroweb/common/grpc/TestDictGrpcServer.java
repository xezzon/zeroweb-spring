package io.github.xezzon.zeroweb.common.grpc;

import com.google.protobuf.Empty;
import io.github.xezzon.zeroweb.auth.JwtAuth;
import io.github.xezzon.zeroweb.auth.JwtClaim;
import io.github.xezzon.zeroweb.dict.DictImportReqList;
import io.github.xezzon.zeroweb.dict.DictListResp;
import io.github.xezzon.zeroweb.dict.DictReq;
import io.github.xezzon.zeroweb.dict.DictServiceGrpc.DictServiceImplBase;
import io.grpc.stub.StreamObserver;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.springframework.grpc.server.service.GrpcService;

/**
 * @author xezzon
 */
@GrpcService
public class TestDictGrpcServer extends DictServiceImplBase {

  @Override
  public void getDictListByTag(DictReq request, StreamObserver<DictListResp> responseObserver) {
    JwtClaim claim = Assertions.assertDoesNotThrow(JwtAuth::getOrThrow);
    Assertions.assertEquals("test", claim.getPreferredUsername());
    responseObserver.onNext(DictListResp.newBuilder()
        .addAllData(Collections.emptyList())
        .build()
    );
    responseObserver.onCompleted();
  }

  @Override
  public void importDict(DictImportReqList request, StreamObserver<Empty> responseObserver) {
    Assertions.assertTrue(JwtAuth.get().isEmpty());
    responseObserver.onNext(Empty.getDefaultInstance());
    responseObserver.onCompleted();
  }
}
