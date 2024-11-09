package io.github.xezzon.geom;

import io.github.xezzon.geom.dict.EnableDictScan;
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
public class GeomOpenApplication {

  /**
   * 开放平台服务入口
   * @param args 应用启动参数
   */
  public static void main(String[] args) {
    SpringApplication.run(GeomOpenApplication.class, args);
  }
}
