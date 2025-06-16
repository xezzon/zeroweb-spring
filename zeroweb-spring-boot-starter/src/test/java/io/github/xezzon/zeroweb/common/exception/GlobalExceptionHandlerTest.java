package io.github.xezzon.zeroweb.common.exception;

import static io.github.xezzon.zeroweb.common.exception.GlobalExceptionHandler.ERROR_CODE_HEADER;

import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;
import java.util.Locale;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class GlobalExceptionHandlerTest {

  static {
    Locale.setDefault(Locale.CHINA);
  }

  @Resource
  private WebTestClient webTestClient;

  @Test
  void repeatDataException() {
    CommonErrorCode errorCode = CommonErrorCode.REPEAT_DATA;
    ErrorResult responseBody = webTestClient.get()
        .uri("/RepeatDataException")
        .exchange()
        .expectStatus().isEqualTo(errorCode.sourceType().getResponseCode())
        .expectHeader().valueEquals(ERROR_CODE_HEADER, errorCode.code())
        .expectBody(ErrorResult.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(RepeatDataException.class.getSimpleName(), responseBody.getCode());
  }

  @Test
  void noValidClasspathException() {
    CommonErrorCode errorCode = CommonErrorCode.UNKNOWN;
    ErrorResult responseBody = webTestClient.get()
        .uri("/NoValidClasspathException")
        .exchange()
        .expectStatus().isEqualTo(errorCode.sourceType().getResponseCode())
        .expectHeader().valueEquals(ERROR_CODE_HEADER, errorCode.code())
        .expectBody(ErrorResult.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(
        NoValidClasspathException.class.getSimpleName(),
        responseBody.getCode()
    );
  }

  @Test
  void entityNotFoundException() {
    CommonErrorCode errorCode = CommonErrorCode.NO_SUCH_DATA;
    ErrorResult responseBody = webTestClient.get()
        .uri("/EntityNotFoundException")
        .exchange()
        .expectStatus().isEqualTo(errorCode.sourceType().getResponseCode())
        .expectHeader().valueEquals(ERROR_CODE_HEADER, errorCode.code())
        .expectBody(ErrorResult.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(
        EntityNotFoundException.class.getSimpleName(),
        responseBody.getCode()
    );
  }

  @Test
  void unsupportedOperationException() {
    CommonErrorCode errorCode = CommonErrorCode.UNKNOWN;
    ErrorResult responseBody = webTestClient.get()
        .uri("/UnsupportedOperationException")
        .exchange()
        .expectStatus().isEqualTo(errorCode.sourceType().getResponseCode())
        .expectHeader().valueEquals(ERROR_CODE_HEADER, errorCode.code())
        .expectBody(ErrorResult.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(
        UnsupportedOperationException.class.getSimpleName(),
        responseBody.getCode()
    );
  }

  @Test
  void methodArgumentNotValidException() {
    CommonErrorCode errorCode = CommonErrorCode.ARGUMENT_NOT_VALID;
    ValidEntity entity = new ValidEntity();
    entity.setName(RandomUtil.randomString(8));
    entity.setEmail(RandomUtil.randomString(8));
    ErrorResult responseBody = webTestClient.post()
        .uri("/MethodArgumentNotValidException")
        .bodyValue(entity)
        .exchange()
        .expectStatus().isEqualTo(errorCode.sourceType().getResponseCode())
        .expectHeader().valueEquals(ERROR_CODE_HEADER, errorCode.code())
        .expectBody(ErrorResult.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(
        MethodArgumentNotValidException.class.getSimpleName(),
        responseBody.getCode()
    );
    Assertions.assertTrue(responseBody.getDetails().stream().anyMatch(detail ->
        Objects.equals("email", detail.getParameters().get("field"))
    ));
    Assertions.assertTrue(responseBody.getDetails().stream().anyMatch(detail ->
        Objects.equals("name", detail.getParameters().get("field"))
    ));
  }

  @Test
  void noResourceFoundException() {
    CommonErrorCode errorCode = CommonErrorCode.NOT_FOUND;
    final String uri = RandomUtil.randomString(8);
    ErrorResult responseBody = webTestClient.get()
        .uri(uri)
        .exchange()
        .expectStatus().isNotFound()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, errorCode.code())
        .expectBody(ErrorResult.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(
        NoResourceFoundException.class.getSimpleName(),
        responseBody.getCode()
    );
  }

  @Test
  void dataPermissionForbiddenException() {
    CommonErrorCode errorCode = CommonErrorCode.DATA_PERMISSION_FORBIDDEN;
    ErrorResult responseBody = webTestClient.get()
        .uri("/DataPermissionForbiddenException")
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, errorCode.code())
        .expectBody(ErrorResult.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(
        DataPermissionForbiddenException.class.getSimpleName(),
        responseBody.getCode()
    );
  }
}
