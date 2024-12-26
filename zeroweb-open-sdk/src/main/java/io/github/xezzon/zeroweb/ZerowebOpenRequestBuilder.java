package io.github.xezzon.zeroweb;

import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.security.Security;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author xezzon
 */
public class ZerowebOpenRequestBuilder {

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  /**
   * 应用访问凭据
   */
  private final String accessKey;
  /**
   * 应用密钥（AES）
   */
  private final byte[] secretKey;

  /**
   * @param accessKey 应用访问凭据
   * @param secretKey 应用密钥
   */
  public ZerowebOpenRequestBuilder(String accessKey, String secretKey) {
    this.accessKey = accessKey;
    this.secretKey = Base64.getDecoder().decode(secretKey);
  }

  public Feign.Builder builder() {
    return Feign.builder()
        .requestInterceptor(new ZerowebOpenRequestInterceptor());
  }

  /**
   * OpenFeign 请求拦截器。向请求头中添加应用访问凭据、时间戳、摘要。
   */
  class ZerowebOpenRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
      // 应用访问凭据
      requestTemplate.header(ZerowebOpenConstant.ACCESS_KEY_HEADER, accessKey);
      // 时间戳
      long timestamp = Instant.now().toEpochMilli();
      requestTemplate.header(ZerowebOpenConstant.TIMESTAMP_HEADER, String.valueOf(timestamp));
      // 摘要
      byte[] body = requestTemplate.body();
      try {
        Mac mac = Mac.getInstance(ZerowebOpenConstant.DIGEST_ALGORITHM);
        mac.init(new SecretKeySpec(secretKey, ZerowebOpenConstant.DIGEST_ALGORITHM));
        mac.update(body);
        String signature = Base64.getEncoder().encodeToString(mac.doFinal());
        requestTemplate.header(ZerowebOpenConstant.SIGNATURE_HEADER, signature);
      } catch (Exception e) {
        throw new ZerowebOpenException(e);
      }
    }
  }
}
