package io.github.xezzon.zeroweb.common.exception;

import static io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant.ERROR_CODE_HEADER;

import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
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
class GlobalExceptionHandlerTest {

  static {
    Locale.setDefault(Locale.CHINA);
  }

  @Resource
  private WebTestClient webTestClient;

  @Test
  void repeatDataException() {
    ErrorResult responseBody = webTestClient.get()
        .uri("/RepeatDataException")
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, RepeatDataException.ERROR_CODE)
        .expectBody(ErrorResult.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals("RepeatData", responseBody.getCode());
  }

  @Test
  void noValidClasspathException() {
    ErrorResult responseBody = webTestClient.get()
        .uri("/NoValidClasspathException")
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.SERVER_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, ErrorCodeConstant.UNKNOWN)
        .expectBody(ErrorResult.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals("NoValidClasspath", responseBody.getCode());
  }

  @Test
  void entityNotFoundException() {
    ErrorResult responseBody = webTestClient.get()
        .uri("/EntityNotFoundException")
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, ErrorCodeConstant.NO_SUCH_DATA)
        .expectBody(ErrorResult.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals("EntityNotFound", responseBody.getCode());
  }

  @Test
  void unsupportedOperationException() {
    ErrorResult responseBody = webTestClient.get()
        .uri("/UnsupportedOperationException")
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.SERVER_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, ErrorCodeConstant.UNKNOWN)
        .expectBody(ErrorResult.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals("UnsupportedOperation", responseBody.getCode());
  }

  @Test
  void methodArgumentNotValidException() {
    ValidEntity entity = new ValidEntity();
    entity.setName(RandomUtil.randomString(8));
    entity.setEmail(RandomUtil.randomString(8));
    ErrorResult responseBody = webTestClient.post()
        .uri("/MethodArgumentNotValidException")
        .bodyValue(entity)
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, ErrorCodeConstant.ARGUMENT_INVALID)
        .expectBody(ErrorResult.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals("MethodArgumentNotValid", responseBody.getCode());
    Assertions.assertTrue(responseBody.getDetails().stream().anyMatch(detail ->
        Objects.equals("email", detail.getParameters().get("field"))
    ));
    Assertions.assertTrue(responseBody.getDetails().stream().anyMatch(detail ->
        Objects.equals("name", detail.getParameters().get("field"))
    ));
  }

  @Test
  void noResourceFoundException() {
    final String uri = RandomUtil.randomString(8);
    webTestClient.get()
        .uri(uri)
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  void dataPermissionForbiddenException() {
    ErrorResult responseBody = webTestClient.get()
        .uri("/DataPermissionForbiddenException")
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, DataPermissionForbiddenException.ERROR_CODE)
        .expectBody(ErrorResult.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals("DataPermissionForbidden", responseBody.getCode());
  }
}
