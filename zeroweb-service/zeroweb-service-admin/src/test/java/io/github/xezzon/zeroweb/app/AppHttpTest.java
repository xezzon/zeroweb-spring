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
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AppHttpTest {

  private static final String ADD_APP_URI = "/app";
  private static final String LIST_APP_URI = "/app";
  private static final String UPDATE_APP_URI = "/app";
  private static final String DELETE_APP_URI = "/app/{id}";

  @Resource
  private RestTestClient testClient;
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
    Id responseBody = testClient.post()
        .uri(ADD_APP_URI)
        .body(req)
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
    testClient.post()
        .uri(ADD_APP_URI)
        .body(req)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS);
  }

  @Test
  void listApp_shouldReturnOk() {
    List<App> apps = repository.findAll();
    // Act & Assert
    List<App> responseBody = testClient.get()
        .uri(LIST_APP_URI)
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<@NonNull List<App>>() {
        })
        .returnResult().getResponseBody();
    assertNotNull(responseBody);
    assertEquals(apps.size(), responseBody.size());
    apps.sort(Comparator.comparing(App::getOrdinal));
    for (int i = 0, cnt = apps.size(); i < cnt; i++) {
      assertEquals(apps.get(i).getId(), responseBody.get(i).getId());
    }
  }

  @Test
  void queryAppById_shouldReturnApp() {
    // Arrange
    List<App> dataset = repository.findAll();
    App expectedApp = dataset.getFirst();

    // Act & Assert
    App responseBody = testClient.get()
        .uri("/app/{id}", expectedApp.getId())
        .exchange()
        .expectStatus().isOk()
        .expectBody(App.class)
        .returnResult().getResponseBody();

    assertNotNull(responseBody);
    assertEquals(expectedApp.getId(), responseBody.getId());
    assertEquals(expectedApp.getName(), responseBody.getName());
    assertEquals(expectedApp.getBaseUrl(), responseBody.getBaseUrl());
    assertEquals(expectedApp.getOrdinal(), responseBody.getOrdinal());
  }

  @Test
  void queryAppById_shouldReturnNotFound_whenAppDoesNotExist() {
    String nonExistentId = "non-existent-id";

    testClient.get()
        .uri("/app/{id}", nonExistentId)
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, ErrorCodeConstant.NO_SUCH_DATA);
  }

  @Test
  void updateApp() {
    // Arrange
    List<App> dataset = repository.findAll();
    App app = dataset.getFirst();
    UpdateAppReq req = new UpdateAppReq(
        app.getId(),
        RandomUtil.randomString(8),
        "http://example.com",
        1
    );

    // Act & Assert for a valid update
    testClient.put()
        .uri(UPDATE_APP_URI)
        .body(req)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void updateApp_invalidBaseUrl() {
    // Arrange
    List<App> dataset = repository.findAll();
    App app = dataset.getFirst();
    UpdateAppReq invalidUrlReq = new UpdateAppReq(
        app.getId(),
        RandomUtil.randomString(8),
        "invalid-url",  // Invalid baseUrl format
        1
    );

    // Act & Assert for invalid baseUrl
    testClient.put()
        .uri(UPDATE_APP_URI)
        .body(invalidUrlReq)
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
    testClient.put()
        .uri(UPDATE_APP_URI)
        .body(nonExistentReq)
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
    App app = dataset.getFirst();
    // Assuming that the app name is optional and can be null
    UpdateAppReq nullOptionalReq = new UpdateAppReq(
        app.getId(),
        null,
        "http://example.com",
        1
    );

    // Act & Assert for updating with null optional fields
    testClient.put()
        .uri(UPDATE_APP_URI)
        .body(nullOptionalReq)
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
    App app = dataset.getFirst();
    int concurrentRequests = 16;
    IntStream.range(0, concurrentRequests).parallel()
        .mapToObj(_ -> new UpdateAppReq(
            app.getId(),
            RandomUtil.randomString(8),
            "http://example.com",
            RandomUtil.randomInt()
        ))
        .forEach(o -> testClient.put()
            .uri(UPDATE_APP_URI)
            .body(o)
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
    String id = dataset.getFirst().getId();

    // Act & Assert
    testClient.delete()
        .uri(uri -> uri.path(DELETE_APP_URI).build(id))
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isOk();

    assertFalse(repository.existsById(id));
    assertEquals(dataset.size() - 1, repository.count());
  }
}
