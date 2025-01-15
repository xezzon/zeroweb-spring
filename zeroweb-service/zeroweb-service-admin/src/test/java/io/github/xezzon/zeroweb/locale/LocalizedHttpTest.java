package io.github.xezzon.zeroweb.locale;

import static io.github.xezzon.zeroweb.common.exception.GlobalExceptionHandler.ERROR_CODE_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.domain.PagedModel;
import io.github.xezzon.zeroweb.common.exception.CommonErrorCode;
import io.github.xezzon.zeroweb.locale.domain.I18nMessage;
import io.github.xezzon.zeroweb.locale.domain.I18nText;
import io.github.xezzon.zeroweb.locale.domain.Language;
import io.github.xezzon.zeroweb.locale.entity.AddI18nMessageReq;
import io.github.xezzon.zeroweb.locale.entity.AddLanguageReq;
import io.github.xezzon.zeroweb.locale.entity.ModifyLanguageReq;
import io.github.xezzon.zeroweb.locale.entity.UpsertI18nTextReq;
import io.github.xezzon.zeroweb.locale.repository.I18nMessageRepository;
import io.github.xezzon.zeroweb.locale.repository.I18nTextRepository;
import io.github.xezzon.zeroweb.locale.repository.LanguageRepository;
import jakarta.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
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
class LocalizedHttpTest {

  private static final String ADD_LANGUAGE_URL = "/language";
  private static final String LIST_LANGUAGE_URL = "/language";
  private static final String UPDATE_LANGUAGE_URL = "/language";
  private static final String DELETE_LANGUAGE_URL = "/language/{id}";
  private static final String ADD_I18N_MESSAGE_URL = "/locale";
  private static final String LIST_I18N_NAMESPACE_URL = "/locale";
  private static final String UPDATE_I18N_MESSAGE_URL = "/locale";
  private static final String DELETE_I18N_MESSAGE_URL = "/locale/{id}";
  private static final String LIST_I18N_MESSAGE_URL = "/locale/{namespace}";
  private static final String QUERY_I18N_TEXT_URL = "/locale/{namespace}/{messageKey}";
  private static final String UPSERT_I18N_TEXT_URL = "/i18n";
  private static final String LOAD_I18N_TEXT_URL = "/i18n/{language}/{namespace}";

  @Resource
  private WebTestClient webTestClient;
  @Resource
  private LanguageRepository languageRepository;
  @Resource
  private I18nMessageRepository i18nMessageRepository;
  @Resource
  private I18nTextRepository i18nTextRepository;

  public void initData() {
    List<Language> languages = languageRepository.findAll().stream()
        .filter(o -> Objects.equals(o.getDictTag(), Language.LANGUAGE_DICT_TAG))
        .toList();
    for (int i = 0; i < 8; i++) {
      String namespace = RandomUtil.randomString(8);
      for (int j = 0, cnt = 8; j < cnt; j++) {
        I18nMessage i18nMessage = new I18nMessage();
        i18nMessage.setNamespace(namespace);
        i18nMessage.setMessageKey(RandomUtil.randomString(8));
        i18nMessageRepository.save(i18nMessage);
        for (Language language : languages) {
          I18nText i18nText = new I18nText();
          i18nText.setNamespace(i18nMessage.getNamespace());
          i18nText.setMessageKey(i18nMessage.getMessageKey());
          i18nText.setLanguage(language.getLanguageTag());
          i18nText.setContent(RandomUtil.randomString(8));
          i18nTextRepository.save(i18nText);
        }
      }
    }
  }

  @AfterEach
  void tearDown() {
    i18nMessageRepository.deleteAll();
    i18nTextRepository.deleteAll();
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
    List<I18nText> dataset = i18nTextRepository.findAll()
        .stream()
        .filter(o -> Objects.equals(o.getLanguage(), Locale.ENGLISH.toLanguageTag()))
        .sorted(Comparator.comparing(I18nText::getId))
        .toList();

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

    Language actualLanguage = languageRepository.findById("3").orElseThrow();
    assertEquals(Locale.GERMAN.toLanguageTag(), actualLanguage.getLanguageTag());
    assertEquals(Locale.GERMAN.getDisplayLanguage(), actualLanguage.getDescription());
    assertEquals(3, actualLanguage.getOrdinal());
    assertEquals(false, actualLanguage.getEnabled());

    List<I18nText> actualDataset = i18nTextRepository.findAll()
        .stream()
        .filter(o -> Objects.equals(o.getLanguage(), Locale.GERMAN.toLanguageTag()))
        .sorted(Comparator.comparing(I18nText::getId))
        .toList();
    assertEquals(dataset.size(), actualDataset.size());
    for (int i = 0; i < dataset.size(); i++) {
      I18nText expect = dataset.get(i);
      I18nText actual = actualDataset.get(i);
      assertEquals(expect.getId(), actual.getId());
    }
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

    assertTrue(i18nTextRepository.findAll()
        .stream()
        .noneMatch(o -> Objects.equals(o.getLanguage(), except.getLanguageTag()))
    );
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
        .distinct()
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
  void queryI18nMessageList() {
    this.initData();
    List<I18nMessage> dataset = i18nMessageRepository.findAll();
    List<I18nMessage> except = dataset.stream()
        .filter(it -> Objects.equals(it.getNamespace(), dataset.get(0).getNamespace()))
        .sorted(Comparator.comparing(I18nMessage::getMessageKey))
        .toList();

    PagedModel<I18nMessage> responseBody = webTestClient.get()
        .uri(builder -> builder.path(LIST_I18N_MESSAGE_URL)
            .queryParam("top", except.size())
            .queryParam("skip", 0)
            .build(dataset.get(0).getNamespace())
        )
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<PagedModel<I18nMessage>>() {
        })
        .returnResult().getResponseBody();
    assertNotNull(responseBody);
    assertEquals(except.size(), responseBody.getPage().getTotalElements());
    assertEquals(except.size(), responseBody.getContent().size());
    for (int i = 0, cnt = except.size(); i < cnt; i++) {
      assertEquals(except.get(i).getId(), responseBody.getContent().get(i).getId());
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
  void updateI18nMessage() throws InterruptedException {
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

    Thread.sleep(3000);
    assertFalse(i18nTextRepository.findAll()
        .stream()
        .filter(o -> Objects.equals(o.getNamespace(), target.getNamespace()))
        .anyMatch(o -> Objects.equals(o.getMessageKey(), target.getMessageKey()))
    );
    assertTrue(i18nTextRepository.findAll()
        .stream()
        .filter(o -> Objects.equals(o.getNamespace(), except.getNamespace()))
        .anyMatch(o -> Objects.equals(o.getMessageKey(), except.getMessageKey()))
    );
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
  void deleteI18nMessage() throws InterruptedException {
    this.initData();
    I18nMessage target = i18nMessageRepository.findAll().get(0);

    webTestClient.delete()
        .uri(builder -> builder.path(DELETE_I18N_MESSAGE_URL)
            .build(target.getId())
        )
        .exchange()
        .expectStatus().isOk();
    assertFalse(i18nMessageRepository.existsById(target.getId()));

    Thread.sleep(3000);
    assertFalse(i18nTextRepository.findAll()
        .stream()
        .filter(o -> Objects.equals(o.getNamespace(), target.getNamespace()))
        .anyMatch(o -> Objects.equals(o.getMessageKey(), target.getMessageKey()))
    );
  }

  @Test
  void queryI18nText() {
    this.initData();
    I18nMessage i18nMessage = i18nMessageRepository.findAll().get(0);

    Map<String, String> responseBody = webTestClient.get()
        .uri(builder -> builder.path(QUERY_I18N_TEXT_URL)
            .build(i18nMessage.getNamespace(), i18nMessage.getMessageKey())
        )
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<Map<String, String>>() {
        })
        .returnResult().getResponseBody();
    Map<String, String> except = i18nTextRepository.findAll()
        .stream()
        .filter(o -> Objects.equals(o.getNamespace(), i18nMessage.getNamespace()))
        .filter(o -> Objects.equals(o.getMessageKey(), i18nMessage.getMessageKey()))
        .collect(Collectors.toMap(I18nText::getLanguage, I18nText::getContent));
    assertNotNull(responseBody);
    for (Entry<String, String> entry : responseBody.entrySet()) {
      assertEquals(except.get(entry.getKey()), entry.getValue());
    }
  }

  @Test
  void insertI18nText() {
    this.initData();
    I18nMessage targetMessage = i18nMessageRepository.findAll().get(0);
    Language targetLanguage = new Language();
    targetLanguage.setLanguageTag(Locale.TAIWAN.toLanguageTag());
    targetLanguage.setDescription(Locale.TAIWAN.getDisplayName());
    targetLanguage.setOrdinal(1);
    targetLanguage.setEnabled(true);
    languageRepository.save(targetLanguage);

    UpsertI18nTextReq req = new UpsertI18nTextReq(
        targetMessage.getNamespace(),
        targetMessage.getMessageKey(),
        targetLanguage.getLanguageTag(),
        RandomUtil.randomString(8)
    );
    Id responseBody = webTestClient.put()
        .uri(UPSERT_I18N_TEXT_URL)
        .bodyValue(req)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Id.class)
        .returnResult().getResponseBody();
    assertNotNull(responseBody);
    I18nText actual = i18nTextRepository.findById(responseBody.id()).orElseThrow();
    assertEquals(req.content(), actual.getContent());
  }

  @Test
  void updateI18nText() {
    this.initData();
    I18nText target = i18nTextRepository.findAll().get(0);

    UpsertI18nTextReq req = new UpsertI18nTextReq(
        target.getNamespace(),
        target.getMessageKey(),
        target.getLanguage(),
        RandomUtil.randomString(8)
    );
    Id responseBody = webTestClient.put()
        .uri(UPSERT_I18N_TEXT_URL)
        .bodyValue(req)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Id.class)
        .returnResult().getResponseBody();
    assertNotNull(responseBody);
    I18nText actual = i18nTextRepository.findById(responseBody.id()).orElseThrow();
    assertNotEquals(target.getContent(), actual.getContent());
    assertEquals(req.content(), actual.getContent());
  }

  @Test
  void updateI18nText_noSuchData_language() {
    this.initData();
    I18nText target = i18nTextRepository.findAll().get(0);

    UpsertI18nTextReq req = new UpsertI18nTextReq(
        target.getNamespace(),
        target.getMessageKey(),
        RandomUtil.randomString(8),
        RandomUtil.randomString(8)
    );
    webTestClient.put()
        .uri(UPSERT_I18N_TEXT_URL)
        .bodyValue(req)
        .exchange()
        .expectStatus().isBadRequest()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, CommonErrorCode.NO_SUCH_DATA.code());
  }

  @Test
  void updateI18nText_noSuchData_namespace() {
    this.initData();
    I18nText target = i18nTextRepository.findAll().get(0);

    UpsertI18nTextReq req = new UpsertI18nTextReq(
        RandomUtil.randomString(8),
        target.getMessageKey(),
        target.getLanguage(),
        RandomUtil.randomString(8)
    );
    webTestClient.put()
        .uri(UPSERT_I18N_TEXT_URL)
        .bodyValue(req)
        .exchange()
        .expectStatus().isBadRequest()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, CommonErrorCode.NO_SUCH_DATA.code());
  }

  @Test
  void updateI18nText_noSuchData_messageKey() {
    this.initData();
    I18nText target = i18nTextRepository.findAll().get(0);

    UpsertI18nTextReq req = new UpsertI18nTextReq(
        target.getNamespace(),
        RandomUtil.randomString(8),
        target.getLanguage(),
        RandomUtil.randomString(8)
    );
    webTestClient.put()
        .uri(UPSERT_I18N_TEXT_URL)
        .bodyValue(req)
        .exchange()
        .expectStatus().isBadRequest()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, CommonErrorCode.NO_SUCH_DATA.code());
  }

  @Test
  void loadI18nText() {
    this.initData();
    List<I18nText> dataset = i18nTextRepository.findAll();
    String targetLanguage = dataset.get(0).getLanguage();
    String targetNamespace = dataset.get(0).getNamespace();

    Map<String, String> responseBody = webTestClient.get()
        .uri(builder -> builder.path(LOAD_I18N_TEXT_URL)
            .build(targetLanguage, targetNamespace)
        )
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<Map<String, String>>() {
        })
        .returnResult().getResponseBody();
    assertNotNull(responseBody);
    for (Entry<String, String> entry : responseBody.entrySet()) {
      assertTrue(dataset.stream()
          .filter(o -> Objects.equals(o.getLanguage(), targetLanguage))
          .filter(o -> Objects.equals(o.getNamespace(), targetNamespace))
          .filter(o -> Objects.equals(o.getMessageKey(), entry.getKey()))
          .anyMatch(o -> Objects.equals(o.getContent(), entry.getValue()))
      );
    }
  }
}
