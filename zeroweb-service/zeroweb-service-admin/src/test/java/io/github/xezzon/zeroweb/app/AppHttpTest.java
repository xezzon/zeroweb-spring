package io.github.xezzon.zeroweb.app;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.xezzon.zeroweb.app.domain.AddAppReq;
import io.github.xezzon.zeroweb.common.domain.Id;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class AppHttpTest {

  private static final String ADD_APP_URI = "/app";

  @Resource
  private WebTestClient webTestClient;

  @Test
  void addApp_shouldReturnId() {
    // Arrange
    AddAppReq req = new AddAppReq("testApp", "http://example.com", 1);

    // Act
    Id responseBody = webTestClient.post()
        .uri(ADD_APP_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Id.class)
        .returnResult().getResponseBody();

    // Assert
    assertNotNull(responseBody);
    assertNotNull(responseBody.id());
  }

  @Test
  void addApp_shouldReturnBadRequest_whenBaseUrlInvalid() {
    // Arrange
    AddAppReq req = new AddAppReq("testApp", "invalid-url", 1);

    // Act & Assert
    webTestClient.post()
        .uri(ADD_APP_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isBadRequest();
  }
}
