package io.github.xezzon.geom.common.exception;

import static io.github.xezzon.geom.common.exception.GlobalExceptionHandler.ERROR_CODE_HEADER;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.geom.core.error.ErrorResponse;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.NoSuchElementException;
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
    Assertions.assertEquals(errorCode.name(), responseBody.error().code());
    Assertions.assertEquals(errorCode.message(), responseBody.error().message());
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
        responseBody.error().code()
    );
    Assertions.assertEquals(errorCode.message(), responseBody.error().message());
  }

  @Test
  void noSuchElementException() {
    ErrorCode errorCode = ErrorCode.NO_SUCH_DATA;
    ErrorResponse responseBody = webTestClient.get()
        .uri("/NoSuchElementException")
        .exchange()
        .expectStatus().isEqualTo(errorCode.sourceType().getResponseCode())
        .expectHeader().valueEquals(ERROR_CODE_HEADER, errorCode.code())
        .expectBody(ErrorResponse.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(errorCode.code(), responseBody.code());
    Assertions.assertEquals(
        NoSuchElementException.class.getSimpleName(),
        responseBody.error().code()
    );
    Assertions.assertEquals(errorCode.message(), responseBody.error().message());
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
        responseBody.error().code()
    );
    Assertions.assertEquals(errorCode.message(), responseBody.error().message());
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
        responseBody.error().code()
    );
    Assertions.assertEquals(errorCode.message(), responseBody.error().message());
    Assertions.assertTrue(responseBody.error().details().parallelStream()
        .anyMatch(detail -> Objects.equals(Email.class.getSimpleName(), detail.code()))
    );
    Assertions.assertTrue(responseBody.error().details().parallelStream()
        .anyMatch(detail -> Objects.equals(Size.class.getSimpleName(), detail.code()))
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
    Assertions.assertEquals(errorCode.name(), responseBody.error().code());
    Assertions.assertEquals(
        String.format("No static resource %s.", uri),
        responseBody.error().message()
    );
  }
}
