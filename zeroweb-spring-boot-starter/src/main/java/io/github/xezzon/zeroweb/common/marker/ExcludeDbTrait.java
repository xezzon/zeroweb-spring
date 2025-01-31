package io.github.xezzon.zeroweb.common.marker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author xezzon
 */
@TestOnly
public class ExcludeDbTrait implements ImportBeanDefinitionRegistrar {

  @Override
  public void registerBeanDefinitions(
      @NotNull AnnotationMetadata metadata,
      @NotNull BeanDefinitionRegistry registry
  ) {
    if (registry instanceof ListableBeanFactory factory) {
      String[] beanNames = factory.getBeanNamesForType(DbTrait.class);
      for (String beanName : beanNames) {
        registry.removeBeanDefinition(beanName);
      }
    }
  }
}
