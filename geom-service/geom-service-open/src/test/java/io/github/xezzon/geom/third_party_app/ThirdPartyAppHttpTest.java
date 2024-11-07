package io.github.xezzon.geom.third_party_app;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static io.github.xezzon.geom.auth.JwtFilter.PUBLIC_KEY_HEADER;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.geom.auth.TestJwtGenerator;
import io.github.xezzon.geom.common.domain.Id;
import io.github.xezzon.geom.third_party_app.domain.AddThirdPartyAppReq;
import io.github.xezzon.geom.third_party_app.domain.ThirdPartyApp;
import io.github.xezzon.geom.third_party_app.repository.ThirdPartyAppRepository;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author xezzon
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class ThirdPartyAppHttpTest {

  private static final String THIRD_PARTY_APP_ADD_URI = "/third-party-app/add";

  @Resource
  private ThirdPartyAppRepository repository;
  @Resource
  private WebTestClient webTestClient;

  @Test
  void addThirdPartyApp() {
    AddThirdPartyAppReq req = new AddThirdPartyAppReq(
        RandomUtil.randomString(8)
    );
    Id responseBody = webTestClient.post()
        .uri(THIRD_PARTY_APP_ADD_URI)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.generateBearer())
        .bodyValue(req)
        .exchange()
        .expectStatus().is2xxSuccessful()
        .expectBody(Id.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertNotNull(responseBody.id());
    Optional<ThirdPartyApp> thirdPartyApp = repository.findById(responseBody.id());
    Assertions.assertTrue(thirdPartyApp.isPresent());
    Assertions.assertEquals(req.name(), thirdPartyApp.get().getName());
  }
}
