package io.github.xezzon.geom.dict.db;

import io.github.xezzon.geom.dict.EnableDictScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xezzon
 */
@SpringBootApplication(scanBasePackages = "io.github.xezzon.geom")
@EnableDictScan
public class DictDbTestApplication {

  public static void main(String[] args) {
    SpringApplication.run(DictDbTestApplication.class, args);
  }
}
