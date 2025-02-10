package io.github.xezzon.zeroweb.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.app.domain.AddAppReq;
import io.github.xezzon.zeroweb.app.domain.App;
import io.github.xezzon.zeroweb.app.domain.UpdateAppReq;
import io.github.xezzon.zeroweb.app.repository.AppRepository;
import io.github.xezzon.zeroweb.common.domain.Id;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
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

  @Resource
  private WebTestClient webTestClient;
  @Resource
  private AppRepository repository;

  public List<App> initData() {
    ArrayList<App> dataset = new ArrayList<>();
    for (int i = 0, cnt = Byte.MAX_VALUE; i < cnt; i++) {
      App openapi = new App();
      openapi.setName(RandomUtil.randomString(8));
      openapi.setBaseUrl(RandomUtil.randomString(8));
      openapi.setOrdinal(RandomUtil.randomInt());
      repository.save(openapi);
      dataset.add(openapi);
    }
    return dataset;
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

  @Test
  void listApp_shouldReturnOk() {
    List<App> apps = this.initData();
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
    List<App> dataset = this.initData();
    App app = dataset.get(0);
    UpdateAppReq req = new UpdateAppReq(
        app.getId(),
        RandomUtil.randomString(8),
        "http://example.com",
        1
    );

    // Act & Assert
    webTestClient.put()
        .uri(UPDATE_APP_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isOk();

    App after = repository.findById(app.getId()).orElseThrow();
    assertEquals(req.name(), after.getName());
    assertEquals(req.baseUrl(), after.getBaseUrl());
    assertEquals(req.ordinal(), after.getOrdinal());
  }
}
