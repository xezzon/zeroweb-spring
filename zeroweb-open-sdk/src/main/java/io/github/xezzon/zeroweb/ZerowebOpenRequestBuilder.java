package io.github.xezzon.zeroweb;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.security.Security;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author xezzon
 */
public class ZerowebOpenRequestBuilder extends Feign.Builder {

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
    this.requestInterceptor(new ZerowebOpenRequestInterceptor());
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
      final long timestamp = Instant.now().toEpochMilli();
      requestTemplate.header(ZerowebOpenConstant.TIMESTAMP_HEADER, String.valueOf(timestamp));
      // 摘要
      try {
        final Mac mac = Mac.getInstance(ZerowebOpenConstant.DIGEST_ALGORITHM);
        mac.init(new SecretKeySpec(secretKey, ZerowebOpenConstant.DIGEST_ALGORITHM));
        final byte[] input = Optional.ofNullable(requestTemplate.body())
            .orElseGet(() -> new byte[0]);
        final byte[] salt = Longs.toByteArray(timestamp);
        mac.update(
            Bytes.concat(input, salt)
        );
        final String signature = Base64.getEncoder().encodeToString(mac.doFinal());
        requestTemplate.header(ZerowebOpenConstant.SIGNATURE_HEADER, signature);
      } catch (Exception e) {
        throw new ZerowebOpenException(e);
      }
    }
  }
}
