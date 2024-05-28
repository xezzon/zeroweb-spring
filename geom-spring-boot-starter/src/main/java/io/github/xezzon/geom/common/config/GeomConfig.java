package io.github.xezzon.geom.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author xezzon
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = GeomConfig.GEOM)
public class GeomConfig {

  public static final String GEOM = "geom";
  public static final String ID_GENERATOR = "id-generator";

  /**
   * ID生成策略
   */
  protected String idGenerator;
}
