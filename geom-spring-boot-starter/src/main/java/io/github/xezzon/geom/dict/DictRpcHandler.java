package io.github.xezzon.geom.dict;

import com.google.protobuf.Empty;
import io.github.xezzon.geom.common.trait.RpcTrait;
import io.github.xezzon.geom.dict.DictGrpc.DictStub;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.jetbrains.annotations.TestOnly;
import org.springframework.stereotype.Component;

/**
 * @author xezzon
 */
@Slf4j
@Component
public class DictRpcHandler implements DictImporter, RpcTrait {

  @TestOnly
  @Getter(onMethod_ = {@TestOnly})
  private final CountDownLatch countDownLatch = new CountDownLatch(1);

  @GrpcClient("dict")
  private DictStub dictStub;

  @Override
  public void importDict(DictImportReqList reqList) {
    dictStub.importDict(reqList, new StreamObserver<>() {
      @Override
      public void onNext(Empty value) {
        log.debug("Finish to import dict with RPC.");
      }

      @Override
      public void onError(Throwable t) {
        log.error("Failed to import dict with RPC.", t);
      }

      @Override
      public void onCompleted() {
        countDownLatch.countDown();
      }
    });
  }
}
