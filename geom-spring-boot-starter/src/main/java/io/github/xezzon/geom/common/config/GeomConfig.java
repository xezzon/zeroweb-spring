package io.github.xezzon.geom.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 本系统相关配置
 * @author xezzon
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = GeomConfig.GEOM)
public class GeomConfig {

  /**
   * 本系统相关配置的前缀
   */
  public static final String GEOM = "geom";
  /**
   * ID生成策略配置Key
   */
  public static final String ID_GENERATOR = "id-generator";

  /**
   * ID生成策略
   */
  protected IdGeneratorEnum idGenerator;
  /**
   * JWT 相关配置
   */
  protected GeomJwtConfig jwt;

  /**
   * JWT 相关配置
   */
  @Getter
  @Setter
  public static class GeomJwtConfig {

    /**
     * JWT 签发机构
     */
    protected String issuer;
    /**
     * JWT 有效时长，单位 秒
     */
    protected Long timeout;
  }

  /**
   * ID生成策略枚举值
   */
  public enum IdGeneratorEnum {
    /**
     * UUID
     */
    UUID,
    ;
  }
}
