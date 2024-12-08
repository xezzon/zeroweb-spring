package io.github.xezzon.zeroweb;

import io.github.xezzon.zeroweb.auth.JwtFilter;
import io.github.xezzon.zeroweb.dict.EnableDictScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

/**
 * 后台管理服务
 * @author xezzon
 */
@SpringBootApplication
@ComponentScan(excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
})
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
@EnableDictScan
public class ZerowebAdminApplication {

  /**
   * 后台管理服务入口
   * @param args 应用启动参数
   */
  public static void main(String[] args) {
    SpringApplication.run(ZerowebAdminApplication.class, args);
  }
}
