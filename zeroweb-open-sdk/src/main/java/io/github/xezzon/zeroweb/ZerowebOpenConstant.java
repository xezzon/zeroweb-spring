package io.github.xezzon.zeroweb;

/**
 * 定义调用对外接口的常量
 * @author xezzon
 */
public class ZerowebOpenConstant {

  /**
   * 摘要算法
   */
  public static final String DIGEST_ALGORITHM = "HmacSHA256";
  /**
   * 应用访问凭据的请求头
   */
  public static final String ACCESS_KEY_HEADER = "X-Access-Key";
  /**
   * 摘要生成时间戳的请求头
   */
  public static final String TIMESTAMP_HEADER = "X-Timestamp";
  /**
   * 摘要的请求头
   */
  public static final String SIGNATURE_HEADER = "X-Signature";

  private ZerowebOpenConstant() {
  }
}
