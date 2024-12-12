package io.github.xezzon.zeroweb.auth;

import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 相关配置
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

  /**
   * 添加注解式鉴权功能
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
  }
}
