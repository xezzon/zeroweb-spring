package io.github.xezzon.zeroweb.common.i18n;

import java.nio.charset.Charset;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Spring 国际化配置
 * @author xezzon
 */
@Configuration
public class I18nConfig {

  /**
   * 国际化文件放在 classpath:i18n/ 目录下，以 messages 开头
   */
  private static final String BASENAME = "i18n.messages";

  /**
   * 用于对 Hibernate Validator 国际化消息的支持
   *
   * @return 国际化工具
   */
  @Bean
  public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename(BASENAME);
    messageSource.setDefaultEncoding(Charset.defaultCharset().name());
    messageSource.setDefaultLocale(Locale.getDefault());
    return messageSource;
  }
}
