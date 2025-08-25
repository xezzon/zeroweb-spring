package io.github.xezzon.zeroweb.app;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;
import static io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant.ERROR_CODE_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.app.entity.AddAppReq;
import io.github.xezzon.zeroweb.app.entity.UpdateAppReq;
import io.github.xezzon.zeroweb.app.repository.AppRepository;
import io.github.xezzon.zeroweb.auth.TestJwtGenerator;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant;
import jakarta.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class AppHttpTest {

  private static final String ADD_APP_URI = "/app";
  private static final String LIST_APP_URI = "/app";
  private static final String UPDATE_APP_URI = "/app";
  private static final String DELETE_APP_URI = "/app/{id}";

  @Resource
  private WebTestClient webTestClient;
  @Resource
  private AppRepository repository;

  @BeforeEach
  void setUp() {
    for (int i = 0, cnt = Byte.MAX_VALUE; i < cnt; i++) {
      App openapi = new App();
      openapi.setName(RandomUtil.randomString(8));
      openapi.setBaseUrl(RandomUtil.randomString(8));
      openapi.setOrdinal(RandomUtil.randomInt());
      repository.save(openapi);
    }
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Test
  void addApp_shouldReturnId() {
    // Arrange
    AddAppReq req = new AddAppReq("testApp", "http://example.com", 1);

    // Act
    Id responseBody = webTestClient.post()
        .uri(ADD_APP_URI)
        .bodyValue(req)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isOk()
        .expectBody(Id.class)
        .returnResult().getResponseBody();

    // Assert
    assertNotNull(responseBody);
    assertNotNull(responseBody.id());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "invalid-url",
      "htp://example.com",
      "://missing-part",
      "http//missingColon.com",
      "www.example.com",    // missing protocol
  })
  void addApp_shouldReturnBadRequest_whenBaseUrlInvalid(String invalidUrl) {
    // Arrange
    AddAppReq req = new AddAppReq("testApp", invalidUrl, 1);

    // Act & Assert
    webTestClient.post()
        .uri(ADD_APP_URI)
        .bodyValue(req)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS);
  }

  @Test
  void listApp_shouldReturnOk() {
    List<App> apps = repository.findAll();
    // Act & Assert
    List<App> responseBody = webTestClient.get()
        .uri(LIST_APP_URI)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(App.class)
        .returnResult().getResponseBody();
    assertNotNull(responseBody);
    assertEquals(apps.size(), responseBody.size());
    apps.sort(Comparator.comparing(App::getOrdinal));
    for (int i = 0, cnt = apps.size(); i < cnt; i++) {
      assertEquals(apps.get(i).getId(), responseBody.get(i).getId());
    }
  }

  @Test
  void updateApp() {
    // Arrange
    List<App> dataset = repository.findAll();
    App app = dataset.get(0);
    UpdateAppReq req = new UpdateAppReq(
        app.getId(),
        RandomUtil.randomString(8),
        "http://example.com",
        1
    );

    // Act & Assert for a valid update
    webTestClient.put()
        .uri(UPDATE_APP_URI)
        .bodyValue(req)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void updateApp_invalidBaseUrl() {
    // Arrange
    List<App> dataset = repository.findAll();
    App app = dataset.get(0);
    UpdateAppReq invalidUrlReq = new UpdateAppReq(
        app.getId(),
        RandomUtil.randomString(8),
        "invalid-url",  // Invalid baseUrl format
        1
    );

    // Act & Assert for invalid baseUrl
    webTestClient.put()
        .uri(UPDATE_APP_URI)
        .bodyValue(invalidUrlReq)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS);
  }

  @Test
  void updateApp_nonExistent() {
    // Arrange
    // Using a non-existent app id for the test
    UpdateAppReq nonExistentReq = new UpdateAppReq(
        "non-existent-id",
        RandomUtil.randomString(8),
        "http://example.com",
        1
    );

    // Act & Assert for non-existent app update
    webTestClient.put()
        .uri(UPDATE_APP_URI)
        .bodyValue(nonExistentReq)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, ErrorCodeConstant.NO_SUCH_DATA);
  }

  @Test
  void updateApp_nullOptionalFields() {
    // Arrange
    List<App> dataset = repository.findAll();
    App app = dataset.get(0);
    // Assuming that the app name is optional and can be null
    UpdateAppReq nullOptionalReq = new UpdateAppReq(
        app.getId(),
        null,
        "http://example.com",
        1
    );

    // Act & Assert for updating with null optional fields
    webTestClient.put()
        .uri(UPDATE_APP_URI)
        .bodyValue(nullOptionalReq)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, ErrorCodeConstant.ARGUMENT_INVALID);
  }

  @Test
  void updateApp_concurrentUpdates() {
    // Arrange
    List<App> dataset = repository.findAll();
    App app = dataset.get(0);
    int concurrentRequests = 16;
    IntStream.range(0, concurrentRequests).parallel()
        .mapToObj(i -> new UpdateAppReq(
            app.getId(),
            RandomUtil.randomString(8),
            "http://example.com",
            RandomUtil.randomInt()
        ))
        .forEach(o -> webTestClient.put()
            .uri(UPDATE_APP_URI)
            .bodyValue(o)
            .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
            .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
            .exchange()
            .expectStatus().isOk()
        );
  }


  @Test
  void deleteApp() {
    // Arrange
    List<App> dataset = repository.findAll();
    String id = dataset.get(0).getId();

    // Act & Assert
    webTestClient.delete()
        .uri(uri -> uri.path(DELETE_APP_URI).build(id))
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isOk();

    assertFalse(repository.existsById(id));
    assertEquals(dataset.size() - 1, repository.count());
  }
}
