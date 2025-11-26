package io.github.xezzon.zeroweb.setting;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;
import static io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant.ERROR_CODE_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.auth.TestJwtGenerator;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.domain.PagedModel;
import io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant;
import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.setting.entity.AddSettingRequest;
import io.github.xezzon.zeroweb.setting.repository.SettingRepository;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SettingHttpTest {

  private static final String ADD_SETTING_URI = "/setting";
  private static final String GET_SETTING_PAGE_URI = "/setting";

  @Resource
  private RestTestClient testClient;
  @Resource
  private SettingRepository repository;

  @BeforeEach
  void setUp() {
    for (int i = 0, cnt = Byte.MAX_VALUE; i < cnt; i++) {
      Setting setting = new Setting();
      setting.setKey(RandomUtil.randomString(8));
      setting.setSchema("{}");
      setting.setValue(Collections.emptyMap());
      setting.setUpdateTime(Instant.now());
      repository.save(setting);
    }
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Test
  void addSetting() {
    AddSettingRequest request = new AddSettingRequest(
        RandomUtil.randomString(9),
        "{}",
        Collections.emptyMap()
    );
    Id responseBody = testClient.post()
        .uri(ADD_SETTING_URI)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .body(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Id.class)
        .returnResult().getResponseBody();

    Assertions.assertNotNull(responseBody);
    Optional<Setting> actual = repository.findById(responseBody.id());
    Assertions.assertTrue(actual.isPresent());
    Assertions.assertEquals(request.key(), actual.get().getKey());
  }

  @Test
  void addSetting_repeat() {
    Setting exist = repository.findAll().getFirst();
    AddSettingRequest request = new AddSettingRequest(
        exist.getKey(),
        "{}",
        Collections.emptyMap()
    );

    testClient.post()
        .uri(ADD_SETTING_URI)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .body(request)
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, RepeatDataException.ERROR_CODE);
  }

  @Test
  void pagedList() {
    final int top = 5;
    final int skip = top * 2;
    List<Setting> dataset = repository.findAll();

    PagedModel<Setting> responseBody = testClient.get()
        .uri(builder -> builder
            .path(GET_SETTING_PAGE_URI)
            .queryParam("top", top)
            .queryParam("skip", skip)
            .build()
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<@NonNull PagedModel<Setting>>() {
        })
        .returnResult().getResponseBody();

    assertNotNull(responseBody);
    assertEquals(dataset.size(), responseBody.getPage().getTotalElements());
    List<Setting> except = dataset.parallelStream()
        .sorted(Comparator.comparing(Setting::getUpdateTime).reversed())
        .skip(skip)
        .limit(top)
        .toList();
    for (int i = 0, cnt = responseBody.getContent().size(); i < cnt; i++) {
      assertEquals(except.get(i).getId(), responseBody.getContent().get(i).getId());
    }
  }
}
