package io.github.xezzon.zeroweb.crypto;

import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CryptoHttpTest {

  private static final String PASSWORD_STRENGTH = "/password-strength";
  @Resource
  private RestTestClient testClient;

  @Test
  void passwordStrength() {
    Map<String, Object> responseBody = testClient.get()
        .uri(builder -> builder
            .path(PASSWORD_STRENGTH)
            .queryParam("password", RandomUtil.randomString(8))
            .build()
        )
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<@NonNull Map<String, Object>>() {
        })
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(2, responseBody.size());
    Integer score = Assertions.assertInstanceOf(Integer.class, responseBody.get("score"));
    Assertions.assertTrue(score >= 0);
    Assertions.assertTrue(score <= 4);
    Double guessesLog10 = Assertions
        .assertInstanceOf(Double.class, responseBody.get("guessesLog10"));
    Assertions.assertTrue(guessesLog10 > 0);
  }
}
