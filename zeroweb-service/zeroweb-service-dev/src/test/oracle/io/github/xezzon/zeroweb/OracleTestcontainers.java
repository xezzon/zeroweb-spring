package io.github.xezzon.zeroweb;

import java.time.Duration;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.oracle.OracleContainer;

/**
 * Oracle Testcontainers 配置
 */
@Configuration(proxyBeanMethods=false)
public class OracleTestcontainers {

  @Container
  static final OracleContainer oracle = new OracleContainer("gvenzl/oracle-free:slim-faststart")
      .withStartupTimeout(Duration.ofMinutes(5));

  static {
    oracle.start();
    System.setProperty("spring.datasource.url", oracle.getJdbcUrl());
    System.setProperty("spring.datasource.username", oracle.getUsername());
    System.setProperty("spring.datasource.password", oracle.getPassword());
    System.setProperty("spring.datasource.driver-class-name", oracle.getDriverClassName());
  }
}
