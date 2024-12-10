package io.github.xezzon.zeroweb.common.context;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 应用上下文
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

  /**
   * 应用上下文
   */
  @Getter
  private static ApplicationContext applicationContext;

  private static void setContext(ApplicationContext context) {
    applicationContext = context;
  }

  @Override
  public void setApplicationContext(@NotNull ApplicationContext applicationContext)
      throws BeansException {
    setContext(applicationContext);
  }
}
