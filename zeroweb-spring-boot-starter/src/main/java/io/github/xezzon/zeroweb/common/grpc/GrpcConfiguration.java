package io.github.xezzon.zeroweb.common.grpc;

import net.devh.boot.grpc.client.channelfactory.GrpcChannelConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xezzon
 */
@Configuration
public class GrpcConfiguration {

  @Bean
  public GrpcChannelConfigurer retryChannelConfigurer() {
    return (channelBuilder, name) -> channelBuilder
        .maxRetryAttempts(3);
  }
}
