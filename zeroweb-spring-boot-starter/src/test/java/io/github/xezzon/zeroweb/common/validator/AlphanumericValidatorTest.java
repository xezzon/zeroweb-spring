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
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AlphanumericValidatorTest {

  static {
    Locale.setDefault(Locale.CHINA);
  }

  @Resource
  private RestTestClient testClient;

  @Test
  void validate_requestBody() {
    ValidEntity entity = new ValidEntity();
    entity.setAlphabet("ABCD@efg.hijk");
    ChildEntity childEntity = new ChildEntity();
    childEntity.setAlphabet("no_rst-uvw");
    entity.setChildEntity(childEntity);
    ErrorResult responseBody = testClient.post()
        .uri("/alphanumeric/validate")
        .body(entity)
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
        .anyMatch(detail ->
            Objects.equals(detail.getParameters().get("field"), "alphabet")
            && Objects.equals(detail.getParameters().get("invalidCharacter"), "@.")
        )
    );
    Assertions.assertTrue(details.stream()
        .anyMatch(detail ->
            Objects.equals(detail.getParameters().get("field"), "childEntity.alphabet")
              && Objects.equals(detail.getParameters().get("invalidCharacter"), "_-")
        )
    );
  }

  @Test
  void validate_requestParam() {
    ErrorResult responseBody = testClient.get()
        .uri(builder -> builder
            .path("/alphanumeric/validate")
            .queryParam("alphabet", "abc@123")
            .build()
        )
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, ErrorCodeConstant.ARGUMENT_INVALID)
        .expectBody(ErrorResult.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals("HandlerMethodValidation", responseBody.getCode());
    List<ErrorResult.Detail> details = responseBody.getDetails();
    Assertions.assertNotNull(details);
    Assertions.assertTrue(details.stream()
        .anyMatch(detail ->
            Objects.equals(detail.getParameters().get("field"), "alphabet")
            && Objects.equals(detail.getParameters().get("invalidCharacter"), "@")
        )
    );
  }
}
