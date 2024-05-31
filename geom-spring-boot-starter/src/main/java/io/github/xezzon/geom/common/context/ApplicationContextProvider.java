package io.github.xezzon.geom.common.context;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

  @Getter
  private static ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(@NotNull ApplicationContext applicationContext)
      throws BeansException {
    ApplicationContextProvider.applicationContext = applicationContext;
  }
}