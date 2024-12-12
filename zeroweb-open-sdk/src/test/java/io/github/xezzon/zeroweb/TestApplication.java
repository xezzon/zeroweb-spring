package io.github.xezzon.zeroweb;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xezzon
 */
@SpringBootApplication
public class TestApplication {

  public static final String SECRET_KEY;

  static {
    try {
      KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
      SecretKey key = keyGenerator.generateKey();
      SECRET_KEY = Base64.getEncoder().encodeToString(key.getEncoded());
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(TestApplication.class, args);
  }
}
