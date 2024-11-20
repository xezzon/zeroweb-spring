package io.github.xezzon.geom;

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
public class GeomOpenRequestBuilder {

  public static final String DIGEST_ALGORITHM = "HmacSHA256";
  public static final String ACCESS_KEY_HEADER = "X-Access-Key";
  public static final String TIMESTAMP_HEADER = "X-Timestamp";
  public static final String SIGNATURE_HEADER = "X-Signature";

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  /**
   * 应用标识
   */
  private final String accessKey;
  /**
   * 应用密钥（AES）
   */
  private final byte[] secretKey;

  public GeomOpenRequestBuilder(String accessKey, String secretKey) {
    this.accessKey = accessKey;
    this.secretKey = Base64.getDecoder().decode(secretKey);
  }

  public Feign.Builder builder() {
    return Feign.builder()
        .requestInterceptor(new GeomOpenRequestInterceptor());
  }

  class GeomOpenRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
      // 应用标识
      requestTemplate.header(ACCESS_KEY_HEADER, accessKey);
      // 时间戳
      long timestamp = Instant.now().toEpochMilli();
      requestTemplate.header(TIMESTAMP_HEADER, String.valueOf(timestamp));
      // 摘要
      byte[] body = requestTemplate.body();
      try {
        Mac mac = Mac.getInstance(DIGEST_ALGORITHM);
        mac.init(new SecretKeySpec(secretKey, DIGEST_ALGORITHM));
        mac.update(body);
        String signature = Base64.getEncoder().encodeToString(mac.doFinal());
        requestTemplate.header(SIGNATURE_HEADER, signature);
      } catch (Exception e) {
        throw new GeomOpenException(e);
      }
    }
  }
}
