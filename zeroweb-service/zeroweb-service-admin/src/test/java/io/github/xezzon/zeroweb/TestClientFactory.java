package io.github.xezzon.zeroweb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * @author xezzon
 */
@Component
public class TestClientFactory {

  @Bean
  @Lazy
  @ConditionalOnClass(RestTestClient.class)
  public RestTestClient restTestClient(@LocalServerPort final int port) {
    return RestTestClient
        .bindToServer()
        .baseUrl("http://localhost:" + port)
        .build();
  }
}
