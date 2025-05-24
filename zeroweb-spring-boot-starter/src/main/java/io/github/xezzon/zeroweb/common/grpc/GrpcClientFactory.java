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
 * @author xezzon
 */
@Component
public class GrpcClientFactory {

  private static final String ADMIN_CHANNEL = "dict";

  @Bean
  public DictBlockingStub dictBlockingStub(final GrpcChannelFactory channels) {
    return DictGrpc.newBlockingStub(
        channels.createChannel(ADMIN_CHANNEL)
    );
  }

  @Bean
  public DictStub dictStub(final GrpcChannelFactory channels) {
    return DictGrpc.newStub(
        channels.createChannel(ADMIN_CHANNEL)
    );
  }

  @Bean
  public UserBlockingStub userBlockingStub(final GrpcChannelFactory channels) {
    return UserGrpc.newBlockingStub(
        channels.createChannel(ADMIN_CHANNEL)
    );
  }
}
