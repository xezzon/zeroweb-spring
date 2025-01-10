package io.github.xezzon.zeroweb.common.validator;

import static io.github.xezzon.zeroweb.common.exception.GlobalExceptionHandler.ERROR_CODE_HEADER;

import io.github.xezzon.zeroweb.common.exception.CommonErrorCode;
import io.github.xezzon.zeroweb.common.exception.ErrorDetail;
import io.github.xezzon.zeroweb.common.exception.ErrorResponse;
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
import org.springframework.web.bind.MethodArgumentNotValidException;

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
    CommonErrorCode errorCode = CommonErrorCode.ARGUMENT_NOT_VALID;
    ValidEntity entity = new ValidEntity();
    entity.setAlphabet("ABCD@efg.hijk");
    ChildEntity childEntity = new ChildEntity();
    childEntity.setAlphabet("no_rst-uvw");
    entity.setChildEntity(childEntity);
    ErrorResponse responseBody = webTestClient.post()
        .uri("/alphanumeric/validate")
        .bodyValue(entity)
        .exchange()
        .expectStatus().isEqualTo(errorCode.sourceType().getResponseCode())
        .expectHeader().valueEquals(ERROR_CODE_HEADER, errorCode.code())
        .expectBody(ErrorResponse.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(errorCode.code(), responseBody.code());
    Assertions.assertEquals(
        MethodArgumentNotValidException.class.getSimpleName(),
        responseBody.error().getCode()
    );
    List<ErrorDetail> details = responseBody.error().getDetails();
    Assertions.assertNotNull(details);
    Assertions.assertEquals(2, details.size());
    Assertions.assertTrue(details.stream()
        .anyMatch(detail ->
            Objects.equals(detail.getCode(), "alphabet")
                && Objects.equals(detail.getMessage(), "不允许的字符@.")
        )
    );
    Assertions.assertTrue(details.stream()
        .anyMatch(detail ->
            Objects.equals(detail.getCode(), "childEntity.alphabet")
                && Objects.equals(detail.getMessage(), "不允许的字符_-")
        )
    );
  }
}
