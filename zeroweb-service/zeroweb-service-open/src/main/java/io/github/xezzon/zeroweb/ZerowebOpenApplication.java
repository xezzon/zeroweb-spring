package io.github.xezzon.zeroweb;

import io.github.xezzon.zeroweb.dict.EnableDictScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

/**
 * 开放平台服务
 * @author xezzon
 */
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
@EnableDictScan
public class ZerowebOpenApplication {

  /**
   * 开放平台服务入口
   * @param args 应用启动参数
   */
  public static void main(String[] args) {
    SpringApplication.run(ZerowebOpenApplication.class, args);
  }
}
