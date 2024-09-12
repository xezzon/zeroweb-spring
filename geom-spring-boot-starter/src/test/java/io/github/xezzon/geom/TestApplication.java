package io.github.xezzon.geom;

import io.github.xezzon.geom.common.trait.ExcludeDbTrait;
import io.github.xezzon.geom.dict.EnableDictScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author xezzon
 */
@SpringBootApplication
@EnableDictScan()
@Import(ExcludeDbTrait.class)
public class TestApplication {

  public static void main(String[] args) {
    SpringApplication.run(TestApplication.class, args);
  }
}
