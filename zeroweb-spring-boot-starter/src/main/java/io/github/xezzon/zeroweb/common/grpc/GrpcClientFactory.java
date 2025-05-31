package io.github.xezzon.zeroweb.common.grpc;

import io.github.xezzon.zeroweb.dict.DictGrpc;
import io.github.xezzon.zeroweb.dict.DictGrpc.DictBlockingStub;
import io.github.xezzon.zeroweb.dict.DictGrpc.DictStub;
import io.github.xezzon.zeroweb.user.UserGrpc;
import io.github.xezzon.zeroweb.user.UserGrpc.UserBlockingStub;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Component;

/**
 * 构造 gRPC 客户端 Bean。
 * @author xezzon
 */
@Component
public class GrpcClientFactory {

  private static final String ADMIN_CHANNEL = "admin";

  /**
   * 构造字典模块的阻塞式调用的 gRPC 客户端
   * @return 字典 gRPC 服务客户端
   */
  @Bean
  DictBlockingStub dictBlockingStub(final GrpcChannelFactory channels) {
    return DictGrpc.newBlockingStub(
        channels.createChannel(ADMIN_CHANNEL)
    );
  }

  /**
   * 构造字典模块的非阻塞式调用的 gRPC 客户端
   * @return 字典 gRPC 服务客户端
   */
  @Bean
  DictStub dictStub(final GrpcChannelFactory channels) {
    return DictGrpc.newStub(
        channels.createChannel(ADMIN_CHANNEL)
    );
  }

  /**
   * 构造用户模块的阻塞式调用的 gRPC 客户端
   * @return 用户 gRPC 服务客户端
   */
  @Bean
  UserBlockingStub userBlockingStub(final GrpcChannelFactory channels) {
    return UserGrpc.newBlockingStub(
        channels.createChannel(ADMIN_CHANNEL)
    );
  }
}
