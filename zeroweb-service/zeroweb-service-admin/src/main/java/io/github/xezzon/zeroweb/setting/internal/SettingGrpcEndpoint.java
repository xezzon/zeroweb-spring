package io.github.xezzon.zeroweb.setting.internal;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Struct;
import com.google.protobuf.Struct.Builder;
import com.google.protobuf.util.JsonFormat;
import io.github.xezzon.zeroweb.common.exception.ZerowebRuntimeException;
import io.github.xezzon.zeroweb.setting.GetSettingRequest;
import io.github.xezzon.zeroweb.setting.Setting;
import io.github.xezzon.zeroweb.setting.SettingGrpc.SettingImplBase;
import io.github.xezzon.zeroweb.setting.SettingItem;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Resource;
import org.springframework.grpc.server.service.GrpcService;
import tools.jackson.databind.ObjectMapper;

/// 业务参数管理
/// @author xezzon
@GrpcService
public class SettingGrpcEndpoint extends SettingImplBase {

  private final SettingService settingService;
  @Resource
  private ObjectMapper objectMapper;

  public SettingGrpcEndpoint(final SettingService settingService) {
    this.settingService = settingService;
  }

  @Override
  public void getSetting(
      final GetSettingRequest request,
      final StreamObserver<SettingItem> responseObserver
  ) {
    try {
      Setting setting = settingService.queryByCode(request.getCode());
      String valueJson = objectMapper.writeValueAsString(setting.getValue());
      Builder valueBuilder = Struct.newBuilder();
      JsonFormat.parser().merge(valueJson, valueBuilder);
      responseObserver.onNext(SettingItem.newBuilder()
          .setCode(setting.getCode())
          .setValue(valueBuilder.build())
          .build()
      );
    } catch (InvalidProtocolBufferException e) {
      throw new ZerowebRuntimeException(e);
    } finally {
      responseObserver.onCompleted();
    }
  }
}
