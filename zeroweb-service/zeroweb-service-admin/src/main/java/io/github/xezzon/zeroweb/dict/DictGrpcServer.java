package io.github.xezzon.zeroweb.dict;

import com.google.protobuf.Empty;
import io.github.xezzon.zeroweb.dict.converter.DictImportReqConverter;
import io.github.xezzon.zeroweb.dict.converter.DictRespConverter;
import io.github.xezzon.zeroweb.dict.domain.Dict;
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

  /**
   * 导入字典数据（服务间接口）
   */
  @Override
  public void importDict(DictImportReqList request, StreamObserver<Empty> responseObserver) {
    List<Dict> dictList = request.getDataList().parallelStream()
        .map(DictImportReqConverter.INSTANCE::from)
        .toList();
    dictService.importDict(dictList);
    responseObserver.onNext(Empty.getDefaultInstance());
    responseObserver.onCompleted();
  }
}
