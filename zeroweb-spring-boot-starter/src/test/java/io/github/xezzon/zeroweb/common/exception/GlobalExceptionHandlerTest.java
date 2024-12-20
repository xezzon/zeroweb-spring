package io.github.xezzon.zeroweb.common.exception;

import static io.github.xezzon.zeroweb.common.exception.GlobalExceptionHandler.ERROR_CODE_HEADER;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.core.error.ErrorResponse;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
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
class GlobalExceptionHandlerTest {

  @Resource
  private WebTestClient webTestClient;

  @Test
  void repeatDataException() {
    ErrorCode errorCode = ErrorCode.REPEAT_DATA;
    ErrorResponse responseBody = webTestClient.get()
        .uri("/RepeatDataException")
        .exchange()
        .expectStatus().isEqualTo(errorCode.sourceType().getResponseCode())
        .expectHeader().valueEquals(ERROR_CODE_HEADER, errorCode.code())
        .expectBody(ErrorResponse.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(errorCode.code(), responseBody.code());
    Assertions.assertEquals(errorCode.name(), responseBody.error().getCode());
    Assertions.assertEquals(errorCode.message(), responseBody.error().getMessage());
  }

  @Test
  void noValidClasspathException() {
    ErrorCode errorCode = ErrorCode.UNKNOWN;
    ErrorResponse responseBody = webTestClient.get()
        .uri("/NoValidClasspathException")
        .exchange()
        .expectStatus().isEqualTo(errorCode.sourceType().getResponseCode())
        .expectHeader().valueEquals(ERROR_CODE_HEADER, errorCode.code())
        .expectBody(ErrorResponse.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(errorCode.code(), responseBody.code());
    Assertions.assertEquals(
        NoValidClasspathException.class.getSimpleName(),
        responseBody.error().getCode()
    );
    Assertions.assertEquals(errorCode.message(), responseBody.error().getMessage());
  }

  @Test
  void entityNotFoundException() {
    ErrorCode errorCode = ErrorCode.NO_SUCH_DATA;
    ErrorResponse responseBody = webTestClient.get()
        .uri("/EntityNotFoundException")
        .exchange()
        .expectStatus().isEqualTo(errorCode.sourceType().getResponseCode())
        .expectHeader().valueEquals(ERROR_CODE_HEADER, errorCode.code())
        .expectBody(ErrorResponse.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(errorCode.code(), responseBody.code());
    Assertions.assertEquals(
        EntityNotFoundException.class.getSimpleName(),
        responseBody.error().getCode()
    );
    Assertions.assertEquals(errorCode.message(), responseBody.error().getMessage());
  }

  @Test
  void unsupportedOperationException() {
    ErrorCode errorCode = ErrorCode.UNKNOWN;
    ErrorResponse responseBody = webTestClient.get()
        .uri("/UnsupportedOperationException")
        .exchange()
        .expectStatus().isEqualTo(errorCode.sourceType().getResponseCode())
        .expectHeader().valueEquals(ERROR_CODE_HEADER, errorCode.code())
        .expectBody(ErrorResponse.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(errorCode.code(), responseBody.code());
    Assertions.assertEquals(
        UnsupportedOperationException.class.getSimpleName(),
        responseBody.error().getCode()
    );
    Assertions.assertEquals(errorCode.message(), responseBody.error().getMessage());
  }

  @Test
  void methodArgumentNotValidException() {
    ErrorCode errorCode = ErrorCode.ARGUMENT_NOT_VALID;
    ValidEntity entity = new ValidEntity();
    entity.setName(RandomUtil.randomString(8));
    entity.setEmail(RandomUtil.randomString(8));
    ErrorResponse responseBody = webTestClient.post()
        .uri("/MethodArgumentNotValidException")
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
    Assertions.assertEquals(errorCode.message(), responseBody.error().getMessage());
    Assertions.assertTrue(responseBody.error().getDetails().parallelStream()
        .anyMatch(detail -> Objects.equals(Email.class.getSimpleName(), detail.getCode()))
    );
    Assertions.assertTrue(responseBody.error().getDetails().parallelStream()
        .anyMatch(detail -> Objects.equals(Size.class.getSimpleName(), detail.getCode()))
    );
  }

  @Test
  void noResourceFoundException() {
    ErrorCode errorCode = ErrorCode.NOT_FOUND;
    final String uri = RandomUtil.randomString(8);
    ErrorResponse responseBody = webTestClient.get()
        .uri(uri)
        .exchange()
        .expectStatus().isNotFound()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, errorCode.code())
        .expectBody(ErrorResponse.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(errorCode.code(), responseBody.code());
    Assertions.assertEquals(errorCode.name(), responseBody.error().getCode());
    Assertions.assertEquals(
        String.format("No static resource %s.", uri),
        responseBody.error().getMessage()
    );
  }

  @Test
  void dataPermissionForbiddenException() {
    ErrorCode errorCode = ErrorCode.DATA_PERMISSION_FORBIDDEN;
    ErrorResponse responseBody = webTestClient.get()
        .uri("/DataPermissionForbiddenException")
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, errorCode.code())
        .expectBody(ErrorResponse.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(errorCode.code(), responseBody.code());
    Assertions.assertEquals(errorCode.name(), responseBody.error().getCode());
    Assertions.assertEquals("禁止访问: 无权访问该应用", responseBody.error().getMessage());
  }
}
