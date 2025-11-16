package io.github.xezzon.zeroweb.setting.internal;

import io.github.xezzon.zeroweb.setting.GetSettingRequest;
import io.github.xezzon.zeroweb.setting.SettingGrpc.SettingImplBase;
import io.github.xezzon.zeroweb.setting.SettingItem;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

/// 业务参数管理
/// @author xezzon
@GrpcService
public class SettingGrpcEndpoint extends SettingImplBase {

  private final SettingService settingService;

  public SettingGrpcEndpoint(final SettingService settingService) {
    this.settingService = settingService;
  }

  @Override
  public void getSetting(
      final GetSettingRequest request,
      final StreamObserver<SettingItem> responseObserver
  ) {
    super.getSetting(request, responseObserver);
  }
}
