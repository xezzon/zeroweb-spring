package io.github.xezzon.zeroweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

/// 研发平台服务
///
/// @author xezzon
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class ZerowebDeveloperApplication {

  private ZerowebDeveloperApplication() {
  }

  static void main(String[] args) {
    SpringApplication.run(ZerowebDeveloperApplication.class, args);
  }
}
