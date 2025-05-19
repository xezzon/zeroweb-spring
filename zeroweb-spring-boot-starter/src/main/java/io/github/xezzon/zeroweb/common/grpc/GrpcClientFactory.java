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

  private static final String DICT_CHANNEL = "dict";
  private static final String USER_CHANNEL = "user";

  @Bean
  public DictBlockingStub dictBlockingStub(GrpcChannelFactory channels) {
    return DictGrpc.newBlockingStub(
        channels.createChannel(DICT_CHANNEL)
    );
  }

  @Bean
  public DictStub dictStub(GrpcChannelFactory channels) {
    return DictGrpc.newStub(
        channels.createChannel(DICT_CHANNEL)
    );
  }

  @Bean
  public UserBlockingStub userBlockingStub(GrpcChannelFactory channels) {
    return UserGrpc.newBlockingStub(
        channels.createChannel(USER_CHANNEL)
    );
  }
}
