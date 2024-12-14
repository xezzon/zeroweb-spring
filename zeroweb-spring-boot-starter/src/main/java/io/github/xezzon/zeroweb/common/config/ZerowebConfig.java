package io.github.xezzon.zeroweb.common.config;

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
@ConfigurationProperties(prefix = ZerowebConfig.ZEROWEB)
public class ZerowebConfig {

  /**
   * 本系统相关配置的前缀
   */
  public static final String ZEROWEB = "zeroweb";
  /**
   * ID生成策略配置Key
   */
  public static final String ID_GENERATOR = "id-generator";

  /**
   * ID生成策略
   */
  protected IdGeneratorEnum idGenerator = IdGeneratorEnum.UUID;
  /**
   * JWT 相关配置
   */
  protected ZerowebJwtConfig jwt = new ZerowebJwtConfig();

  /**
   * JWT 相关配置
   */
  @Getter
  @Setter
  public static class ZerowebJwtConfig {

    /**
     * JWT 签发机构
     */
    protected String issuer = "xezzon.github.io";
    /**
     * JWT 有效时长，单位 秒
     */
    protected Long timeout = 120L;
  }

  /**
   * ID生成策略枚举值
   */
  public enum IdGeneratorEnum {
    /**
     * UUID
     */
    UUID,
  }
}
