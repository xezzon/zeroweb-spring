package io.github.xezzon.geom.dict;

import io.github.xezzon.geom.dict.converter.DictRespConverter;
import io.github.xezzon.geom.dict.domain.Dict;
import io.grpc.stub.StreamObserver;
import java.util.List;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * @author xezzon
 */
@GrpcService
public class DictGrpcServer extends DictGrpc.DictImplBase {

  private final DictService dictService;

  public DictGrpcServer(DictService dictService) {
    this.dictService = dictService;
  }

  /**
   * 查询指定字典目下所有字典项的列表（服务间接口）
   */
  @Override
  public void getDictListByTag(DictReq request, StreamObserver<DictListResp> responseObserver) {
    List<Dict> dictItemList = dictService.getDictItemList(request.getTag());
    List<DictResp> dictRespList = dictItemList.parallelStream()
        .map(DictRespConverter.INSTANCE::from)
        .toList();
    responseObserver.onNext(DictListResp.newBuilder()
        .addAllData(dictRespList)
        .build()
    );
    responseObserver.onCompleted();
  }
}
