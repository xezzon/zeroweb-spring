package io.github.xezzon.zeroweb.common.validator;

import static io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant.ERROR_CODE_HEADER;

import io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant;
import io.github.xezzon.zeroweb.common.exception.ErrorResult;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class AlphanumericValidatorTest {

  static {
    Locale.setDefault(Locale.CHINA);
  }

  @Resource
  private WebTestClient webTestClient;

  @Test
  void validate() {
    ValidEntity entity = new ValidEntity();
    entity.setAlphabet("ABCD@efg.hijk");
    ChildEntity childEntity = new ChildEntity();
    childEntity.setAlphabet("no_rst-uvw");
    entity.setChildEntity(childEntity);
    ErrorResult responseBody = webTestClient.post()
        .uri("/alphanumeric/validate")
        .bodyValue(entity)
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, ErrorCodeConstant.ARGUMENT_INVALID)
        .expectBody(ErrorResult.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals("MethodArgumentNotValid", responseBody.getCode());
    List<ErrorResult.Detail> details = responseBody.getDetails();
    Assertions.assertNotNull(details);
    Assertions.assertEquals(2, details.size());
    Assertions.assertTrue(details.stream()
        .anyMatch(detail -> Objects.equals(
            detail.getParameters().get("field"),
            "alphabet"
        ))
    );
    Assertions.assertTrue(details.stream()
        .anyMatch(detail -> Objects.equals(
            detail.getParameters().get("field"),
            "childEntity.alphabet"
        ))
    );
  }
}
