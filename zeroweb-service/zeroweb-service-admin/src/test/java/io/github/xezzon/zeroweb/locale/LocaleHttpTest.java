package io.github.xezzon.zeroweb.locale;

import static io.github.xezzon.zeroweb.common.exception.GlobalExceptionHandler.ERROR_CODE_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.exception.CommonErrorCode;
import io.github.xezzon.zeroweb.locale.domain.Language;
import io.github.xezzon.zeroweb.locale.entity.AddLanguageReq;
import io.github.xezzon.zeroweb.locale.entity.ModifyLanguageReq;
import io.github.xezzon.zeroweb.locale.repository.LanguageRepository;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
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
class LocaleHttpTest {

  private static final String ADD_LANGUAGE_URL = "/language";
  private static final String LIST_LANGUAGE_URL = "/language";
  private static final String UPDATE_LANGUAGE_URL = "/language";

  @Resource
  private WebTestClient webTestClient;
  @Resource
  private LanguageRepository languageRepository;

  @Test
  void addLanguage() {
    Locale locale = Locale.JAPANESE;
    AddLanguageReq addLanguageReq = new AddLanguageReq(
        locale.toLanguageTag(),
        locale.getDisplayName(),
        RandomUtil.randomInt(),
        null
    );
    Id responseBody = webTestClient.post()
        .uri(ADD_LANGUAGE_URL)
        .bodyValue(addLanguageReq)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Id.class)
        .returnResult().getResponseBody();
    assertNotNull(responseBody);
    assertNotNull(responseBody.id());
    io.github.xezzon.zeroweb.locale.domain.Language language = languageRepository.findById(
        responseBody.id()).orElseThrow();
    assertEquals(true, language.getEnabled());
  }

  @Test
  void addLanguage_repeat() {
    AddLanguageReq req = new AddLanguageReq(
        Locale.CHINA.toLanguageTag(),
        Locale.CHINA.getDisplayName(),
        RandomUtil.randomInt(10),
        true
    );
    webTestClient.post()
        .uri(ADD_LANGUAGE_URL)
        .bodyValue(req)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(CommonErrorCode.REPEAT_DATA.code());
  }

  @Test
  void queryLanguageList() {
    List<Language> responseBody = webTestClient.get()
        .uri(LIST_LANGUAGE_URL)
        .exchange()
        .expectBodyList(Language.class)
        .returnResult().getResponseBody();
    assertNotNull(responseBody);
    assertEquals(2, responseBody.size());
    assertEquals(Locale.CHINA.toLanguageTag(), responseBody.get(0).getLanguageTag());
  }

  @Test
  void updateLanguage() {
    ModifyLanguageReq req = new ModifyLanguageReq(
        "3",
        Locale.GERMAN.toLanguageTag(),
        Locale.GERMAN.getDisplayLanguage(),
        3,
        false
    );
    webTestClient.put()
        .uri(UPDATE_LANGUAGE_URL)
        .bodyValue(req)
        .exchange()
        .expectStatus().isOk();

    Language actual = languageRepository.findById("3").orElseThrow();
    assertEquals(Locale.GERMAN.toLanguageTag(), actual.getLanguageTag());
    assertEquals(Locale.GERMAN.getDisplayLanguage(), actual.getDescription());
    assertEquals(3, actual.getOrdinal());
    assertEquals(false, actual.getEnabled());
  }

  @Test
  void updateLanguage_noSuchData() {
    ModifyLanguageReq req = new ModifyLanguageReq(
        UUID.randomUUID().toString(),
        Locale.GERMAN.toLanguageTag(),
        Locale.GERMAN.getDisplayLanguage(),
        3,
        false
    );
    webTestClient.put()
        .uri(UPDATE_LANGUAGE_URL)
        .bodyValue(req)
        .exchange()
        .expectStatus().isBadRequest()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, CommonErrorCode.NO_SUCH_DATA.code());
  }

  @Test
  void updateLanguage_repeat() {
    ModifyLanguageReq req = new ModifyLanguageReq(
        "3",
        Locale.CHINA.toLanguageTag(),
        Locale.GERMAN.getDisplayLanguage(),
        3,
        false
    );
    webTestClient.put()
        .uri(UPDATE_LANGUAGE_URL)
        .bodyValue(req)
        .exchange()
        .expectStatus().isBadRequest()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, CommonErrorCode.REPEAT_DATA.code());
  }
}
