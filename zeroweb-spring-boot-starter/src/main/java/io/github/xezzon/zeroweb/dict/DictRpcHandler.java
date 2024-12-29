package io.github.xezzon.zeroweb.dict;

import com.google.protobuf.Empty;
import io.github.xezzon.zeroweb.common.trait.RpcTrait;
import io.github.xezzon.zeroweb.dict.DictGrpc.DictStub;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.jetbrains.annotations.TestOnly;
import org.springframework.stereotype.Component;

/**
 * 以 RPC 调用的方式实现字典导入功能，针对 zeroweb-service-admin 以外的服务。
 * @author xezzon
 */
@Slf4j
@Component
public class DictRpcHandler implements DictImporter, RpcTrait {

  @TestOnly
  private final CountDownLatch countDownLatch = new CountDownLatch(1);

  @GrpcClient("dict")
  private DictStub dictStub;

  @TestOnly
  public CountDownLatch getCountDownLatch() {
    return this.countDownLatch;
  }

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
