package io.github.xezzon.zeroweb.locale;

import static io.github.xezzon.zeroweb.common.exception.GlobalExceptionHandler.ERROR_CODE_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.exception.CommonErrorCode;
import io.github.xezzon.zeroweb.locale.domain.I18nMessage;
import io.github.xezzon.zeroweb.locale.domain.Language;
import io.github.xezzon.zeroweb.locale.entity.AddI18nMessageReq;
import io.github.xezzon.zeroweb.locale.entity.AddLanguageReq;
import io.github.xezzon.zeroweb.locale.entity.ModifyLanguageReq;
import io.github.xezzon.zeroweb.locale.repository.I18nMessageRepository;
import io.github.xezzon.zeroweb.locale.repository.LanguageRepository;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
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
  private static final String DELETE_LANGUAGE_URL = "/language/{id}";
  private static final String ADD_I18N_MESSAGE_URL = "/locale";
  private static final String LIST_I18N_NAMESPACE_URL = "/locale";
  private static final String UPDATE_I18N_MESSAGE_URL = "/locale";
  private static final String DELETE_I18N_MESSAGE_URL = "/locale/{id}";

  @Resource
  private WebTestClient webTestClient;
  @Resource
  private LanguageRepository languageRepository;
  @Resource
  private I18nMessageRepository i18nMessageRepository;

  public void initData() {
    for (int i = 0; i < 16; i++) {
      I18nMessage i18nMessage = new I18nMessage();
      i18nMessage.setNamespace(RandomUtil.randomString(8));
      i18nMessage.setMessageKey(RandomUtil.randomString(8));
      i18nMessageRepository.save(i18nMessage);
    }
  }

  @AfterEach
  void tearDown() {
    i18nMessageRepository.deleteAll();
  }

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
    Language language = languageRepository.findById(responseBody.id()).orElseThrow();
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

  @Test
  void deleteLanguage() {
    Language except = new Language();
    except.setLanguageTag(Locale.FRENCH.toLanguageTag());
    except.setDescription(Locale.FRENCH.getDisplayName());
    except.setOrdinal(16);
    except.setEnabled(true);
    languageRepository.save(except);

    webTestClient.delete()
        .uri(builder -> builder.path(DELETE_LANGUAGE_URL)
            .build(except.getId())
        )
        .exchange()
        .expectStatus().isOk();

    Optional<Language> actual = languageRepository.findById(except.getId());
    assertFalse(actual.isPresent());
  }

  @Test
  void addI18nMessage() {
    AddI18nMessageReq req = new AddI18nMessageReq(
        RandomUtil.randomString(8),
        RandomUtil.randomString(8)
    );
    Id responseBody = webTestClient.post()
        .uri(ADD_I18N_MESSAGE_URL)
        .bodyValue(req)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Id.class)
        .returnResult().getResponseBody();
    assertNotNull(responseBody);
    i18nMessageRepository.findById(responseBody.id()).orElseThrow();
  }

  @Test
  void listI18nMessageNamespace() {
    this.initData();
    List<String> except = i18nMessageRepository.findAll()
        .stream()
        .map(I18nMessage::getNamespace)
        .sorted()
        .toList();

    List<String> responseBody = webTestClient.get()
        .uri(LIST_I18N_NAMESPACE_URL)
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<List<String>>() {
        })
        .returnResult().getResponseBody();
    assertNotNull(responseBody);
    assertEquals(except.size(), responseBody.size());
    for (int i = 0, cnt = except.size(); i < cnt; i++) {
      assertEquals(except.get(i), responseBody.get(i));
    }
  }

  @Test
  void addI18nMessage_repeat() {
    this.initData();
    I18nMessage except = i18nMessageRepository.findAll().get(0);
    AddI18nMessageReq req = new AddI18nMessageReq(
        except.getNamespace(),
        except.getMessageKey()
    );
    webTestClient.post()
        .uri(ADD_I18N_MESSAGE_URL)
        .bodyValue(req)
        .exchange()
        .expectStatus().isBadRequest()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, CommonErrorCode.REPEAT_DATA.code());
  }

  @Test
  void updateI18nMessage() {
    this.initData();
    I18nMessage target = i18nMessageRepository.findAll().get(0);

    I18nMessage except = new I18nMessage();
    except.setId(target.getId());
    except.setNamespace(RandomUtil.randomString(6));
    except.setMessageKey(RandomUtil.randomString(6));
    webTestClient.put()
        .uri(UPDATE_I18N_MESSAGE_URL)
        .bodyValue(except)
        .exchange()
        .expectStatus().isOk();
    I18nMessage actual = i18nMessageRepository.findById(target.getId()).orElseThrow();
    assertEquals(except.getNamespace(), actual.getNamespace());
    assertEquals(except.getMessageKey(), actual.getMessageKey());
  }

  @Test
  void updateI18nMessage_repeat() {
    this.initData();
    I18nMessage target = i18nMessageRepository.findAll().get(0);
    I18nMessage repeat = i18nMessageRepository.findAll().get(1);

    I18nMessage except = new I18nMessage();
    except.setId(target.getId());
    except.setNamespace(repeat.getNamespace());
    except.setMessageKey(repeat.getMessageKey());
    webTestClient.put()
        .uri(UPDATE_I18N_MESSAGE_URL)
        .bodyValue(except)
        .exchange()
        .expectStatus().isBadRequest()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, CommonErrorCode.REPEAT_DATA.code());
  }

  @Test
  void deleteI18nMessage() {
    this.initData();
    I18nMessage target = i18nMessageRepository.findAll().get(0);

    webTestClient.delete()
        .uri(builder -> builder.path(DELETE_I18N_MESSAGE_URL)
            .build(target.getId())
        )
        .exchange()
        .expectStatus().isOk();
    assertFalse(i18nMessageRepository.existsById(target.getId()));
  }
}
