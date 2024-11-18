package io.github.xezzon.geom;

import static io.github.xezzon.geom.GeomOpenRequestBuilder.DIGEST_ALGORITHM;
import static io.github.xezzon.geom.TestApplication.SECRET_KEY;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Assertions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xezzon
 */
@RestController
public class TestController {

  @Resource
  private ObjectMapper objectMapper;

  @PostMapping("/test")
  public String test(
      @RequestBody byte[] body,
      @RequestHeader(GeomOpenRequestBuilder.ACCESS_KEY_HEADER) String accessKey,
      @RequestHeader(GeomOpenRequestBuilder.TIMESTAMP_HEADER) Long timestamp,
      @RequestHeader(GeomOpenRequestBuilder.SIGNATURE_HEADER) String signature
  ) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
    Assertions.assertEquals("hello", accessKey);
    Instant instant = Instant.ofEpochSecond(timestamp);
    Assertions.assertTrue(instant.isBefore(Instant.now()));
    Assertions.assertTrue(Duration.between(instant, Instant.now()).toMinutes() < 2);
    Mac mac = Mac.getInstance(DIGEST_ALGORITHM);
    mac.init(new SecretKeySpec(Base64.getDecoder().decode(SECRET_KEY), DIGEST_ALGORITHM));
    mac.update(body);
    Assertions.assertArrayEquals(Base64.getDecoder().decode(signature), mac.doFinal());
    Entity entity = objectMapper.readValue(body, Entity.class);
    return "Hello, " + entity.name();
  }
}
