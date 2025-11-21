package io.github.xezzon.zeroweb.metadata;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.AUTHORIZATION;

import io.github.xezzon.zeroweb.auth.TestJwtGenerator;
import jakarta.annotation.Resource;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class MetadataHttpTest {

  @Resource
  private RestTestClient testClient;

  @Test
  void serviceInfo() {
    ServiceInfo responseBody = testClient.get()
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
    List<MenuInfo> responseBody = testClient.get()
        .uri("/metadata/menu.json")
        .exchange()
        .expectBody(new ParameterizedTypeReference<@NotNull List<MenuInfo>>() {
        })
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
