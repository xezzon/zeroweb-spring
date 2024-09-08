package io.github.xezzon.geom.dict;

import io.github.xezzon.geom.common.trait.RpcTrait;
import io.github.xezzon.geom.dict.DictGrpc.DictStub;
import io.grpc.internal.testing.StreamRecorder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

/**
 * @author xezzon
 */
@Component
public class DictRpcHandler implements DictImporter, RpcTrait {

  @GrpcClient("dict")
  private DictStub dictStub;

  @Override
  public void importDict(DictImportReqList reqList) {
    dictStub.importDict(reqList, StreamRecorder.create());
  }
}
