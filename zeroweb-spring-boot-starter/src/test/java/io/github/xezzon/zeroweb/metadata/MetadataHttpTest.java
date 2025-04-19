package io.github.xezzon.zeroweb.metadata;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;

import io.github.xezzon.zeroweb.auth.TestJwtGenerator;
import jakarta.annotation.Resource;
import java.util.List;
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
class MetadataHttpTest {

  @Resource
  private WebTestClient webTestClient;

  @Test
  void serviceInfo() {
    ServiceInfo responseBody = webTestClient.get()
        .uri("/metadata/info.json")
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectBody(ServiceInfo.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals("zeroweb-spring-boot-starter", responseBody.getName());
    Assertions.assertEquals("1.0.0", responseBody.getVersion());
    Assertions.assertEquals(ServiceType.SERVER, responseBody.getType());
    Assertions.assertTrue(responseBody.isHidden());
  }

  @Test
  void resourceInfo() {
    List<MenuInfo> responseBody = webTestClient.get()
        .uri("/metadata/menu.json")
        .exchange()
        .expectBodyList(MenuInfo.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(MenuService.MENU_INFOS.size(), responseBody.size());
    for (int i = 0, cnt = MenuService.MENU_INFOS.size(); i < cnt; i++) {
      MenuInfo except = MenuService.MENU_INFOS.get(i);
      MenuInfo actual = responseBody.get(i);
      Assertions.assertEquals(except.getType(), actual.getType());
      Assertions.assertEquals(except.getPath(), actual.getPath());
      Assertions.assertEquals(
          String.join(",", except.getPermissions()),
          String.join(",", actual.getPermissions())
      );
    }
  }
}
