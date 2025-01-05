package io.github.xezzon.rpc;

import io.github.xezzon.zeroweb.common.marker.ExcludeDbTrait;
import io.github.xezzon.zeroweb.dict.EnableDictScan;
import io.github.xezzon.zeroweb.dict.db.DictDbTestApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;

/**
 * @author xezzon
 */
@SpringBootApplication(scanBasePackages = "io.github.xezzon.zeroweb")
@Import(ExcludeDbTrait.class)
@EnableDictScan
public class DictRpcTestApplication implements ImportBeanDefinitionRegistrar {

  public static void main(String[] args) {
    SpringApplication.run(DictDbTestApplication.class, args);
  }
}
