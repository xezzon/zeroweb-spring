package io.github.xezzon.geom.third_party_app;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static io.github.xezzon.geom.auth.JwtFilter.PUBLIC_KEY_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.geom.auth.TestJwtGenerator;
import io.github.xezzon.geom.common.domain.PagedModel;
import io.github.xezzon.geom.third_party_app.domain.AccessSecret;
import io.github.xezzon.geom.third_party_app.domain.AddThirdPartyAppReq;
import io.github.xezzon.geom.third_party_app.domain.ThirdPartyApp;
import io.github.xezzon.geom.third_party_app.repository.AccessSecretRepository;
import io.github.xezzon.geom.third_party_app.repository.ThirdPartyAppRepository;
import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
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
  private static final String THIRD_PARTY_LIST_MINE_API = "/third-party-app/mine";
  private static final String THIRD_PARTY_LIST_API = "/third-party-app";
  private static final String ROLL_ACCESS_SECRET_URI = "/third-party-app/{appId}/roll";

  @Resource
  private ThirdPartyAppRepository repository;
  @Resource
  private AccessSecretRepository accessSecretRepository;
  @Resource
  private WebTestClient webTestClient;

  public List<ThirdPartyApp> initData() {
    List<ThirdPartyApp> dataset = new ArrayList<>();
    for (int i = 0, cnt = 8; i < cnt; i++) {
      String userId = UUID.randomUUID().toString();
      for (int j = 0; j < cnt; j++) {
        ThirdPartyApp thirdPartyApp = new ThirdPartyApp();
        thirdPartyApp.setName(RandomUtil.randomString(8));
        thirdPartyApp.setOwnerId(userId);
        repository.save(thirdPartyApp);
        accessSecretRepository
            .updateSecretKeyById(thirdPartyApp.getId(), RandomUtil.randomString(8));
        dataset.add(thirdPartyApp);
      }
    }
    return dataset;
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Test
  void addThirdPartyApp() {
    AddThirdPartyAppReq req = new AddThirdPartyAppReq(
        RandomUtil.randomString(8)
    );
    AccessSecret responseBody = webTestClient.post()
        .uri(THIRD_PARTY_APP_ADD_URI)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.generateBearer())
        .bodyValue(req)
        .exchange()
        .expectStatus().is2xxSuccessful()
        .expectBody(AccessSecret.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertNotNull(responseBody.getId());
    Optional<ThirdPartyApp> thirdPartyApp = repository.findById(responseBody.getId());
    Assertions.assertTrue(thirdPartyApp.isPresent());
    Assertions.assertEquals(req.name(), thirdPartyApp.get().getName());
    AccessSecret accessSecret = accessSecretRepository.findById(responseBody.getId()).get();
    Assertions.assertEquals(accessSecret.getSecretKey(), responseBody.getSecretKey());
    Assertions.assertArrayEquals(
        responseBody.getId().getBytes(StandardCharsets.UTF_8),
        Base64.getDecoder().decode(accessSecret.getAccessKey())
    );
  }

  @Test
  void listMyThirdPartyApp() {
    final int top = 2;
    final int skip = top * 2;
    List<ThirdPartyApp> dataset = this.initData();
    String me = dataset.get(0).getOwnerId();

    PagedModel<ThirdPartyApp> responseBody = webTestClient.get()
        .uri(builder -> builder
            .path(THIRD_PARTY_LIST_MINE_API)
            .queryParam("top", top)
            .queryParam("skip", skip)
            .build()
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.generateBearer(me))
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<PagedModel<ThirdPartyApp>>() {
        })
        .returnResult().getResponseBody();

    Assertions.assertNotNull(responseBody);
    dataset = dataset.parallelStream()
        .filter(o -> Objects.equals(o.getOwnerId(), me))
        .toList();
    Assertions.assertEquals(dataset.size(), responseBody.getPage().getTotalElements());
    List<ThirdPartyApp> except = dataset.parallelStream()
        .sorted(Comparator.comparing(ThirdPartyApp::getCreateTime).reversed())
        .skip(skip)
        .limit(top)
        .toList();
    Assertions.assertEquals(except.size(), responseBody.getContent().size());
    for (int i = 0, cnt = responseBody.getContent().size(); i < cnt; i++) {
      assertEquals(except.get(i).getId(), responseBody.getContent().get(i).getId());
    }
  }

  @Test
  void listThirdPartyApp() {
    final int top = 5;
    final int skip = top * 2;
    List<ThirdPartyApp> dataset = this.initData();

    PagedModel<ThirdPartyApp> responseBody = webTestClient.get()
        .uri(builder -> builder
            .path(THIRD_PARTY_LIST_API)
            .queryParam("top", top)
            .queryParam("skip", skip)
            .build()
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.generateBearer())
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<PagedModel<ThirdPartyApp>>() {
        })
        .returnResult().getResponseBody();

    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(dataset.size(), responseBody.getPage().getTotalElements());
    List<ThirdPartyApp> except = dataset.parallelStream()
        .sorted(Comparator.comparing(ThirdPartyApp::getCreateTime).reversed())
        .skip(skip)
        .limit(top)
        .toList();
    Assertions.assertEquals(except.size(), responseBody.getContent().size());
    for (int i = 0, cnt = responseBody.getContent().size(); i < cnt; i++) {
      assertEquals(except.get(i).getId(), responseBody.getContent().get(i).getId());
    }
  }

  @Test
  void rollAccessSecret() {
    ThirdPartyApp target = this.initData().get(0);
    AccessSecret responseBody = webTestClient.patch()
        .uri(builder -> builder
            .path(ROLL_ACCESS_SECRET_URI)
            .build(target.getId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.generateBearer(target.getOwnerId()))
        .exchange()
        .expectStatus().is2xxSuccessful()
        .expectBody(AccessSecret.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertNotNull(responseBody.getId());
    AccessSecret accessSecret = accessSecretRepository.findById(responseBody.getId()).get();
    Assertions.assertEquals(accessSecret.getSecretKey(), responseBody.getSecretKey());
    Assertions.assertArrayEquals(
        responseBody.getId().getBytes(StandardCharsets.UTF_8),
        Base64.getDecoder().decode(accessSecret.getAccessKey())
    );
  }
}
