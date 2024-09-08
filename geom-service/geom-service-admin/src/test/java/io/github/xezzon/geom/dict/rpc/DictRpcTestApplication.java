package io.github.xezzon.geom.dict.rpc;

import io.github.xezzon.geom.dict.DictDbHandler;
import io.github.xezzon.geom.dict.EnableDictScan;
import io.github.xezzon.geom.dict.db.DictDbTestApplication;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author xezzon
 */
@SpringBootApplication(
    scanBasePackages = "io.github.xezzon.geom"
)
@EnableDictScan
public class DictRpcTestApplication implements ImportBeanDefinitionRegistrar {

  public static void main(String[] args) {
    SpringApplication.run(DictDbTestApplication.class, args);
  }

  @Override
  public void registerBeanDefinitions(
      @NotNull AnnotationMetadata metadata,
      @NotNull BeanDefinitionRegistry registry
  ) {
    registry.removeBeanDefinition(DictDbHandler.class.getSimpleName());
  }
}
