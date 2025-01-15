package io.github.xezzon.zeroweb.locale;

import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.locale.domain.I18nMessage;
import io.github.xezzon.zeroweb.locale.domain.I18nText;
import io.github.xezzon.zeroweb.locale.domain.Language;
import io.github.xezzon.zeroweb.locale.repository.I18nTextRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class LocalizedService {

  private final LanguageDAO languageDAO;
  private final I18nMessageDAO i18nMessageDAO;
  private final I18nTextDAO i18nTextDAO;
  private final I18nTextRepository i18nTextRepository;

  LocalizedService(
      LanguageDAO languageDAO,
      I18nMessageDAO i18nMessageDAO,
      I18nTextDAO i18nTextDAO,
      I18nTextRepository i18nTextRepository) {
    this.languageDAO = languageDAO;
    this.i18nMessageDAO = i18nMessageDAO;
    this.i18nTextDAO = i18nTextDAO;
    this.i18nTextRepository = i18nTextRepository;
  }

  /**
   * 新增语言
   *
   * @param language 语言
   * @throws RepeatDataException 重复数据异常
   */
  void addLanguage(Language language) {
    /* 前置校验 */
    checkRepeat(language);
    /* 持久化 */
    languageDAO.get().save(language);
  }

  /**
   * 查询语言列表
   *
   * @return 语言列表
   */
  List<Language> queryLanguageList() {
    return languageDAO.findAllOrderByOrdinalAsc();
  }

  /**
   * 更新语言
   *
   * @param language 语言
   */
  void updateLanguage(Language language) {
    Language entity = languageDAO.get().getReferenceById(language.getId());
    languageDAO.getCopier().copy(language, entity);
    /* 前置校验 */
    this.checkRepeat(language);
    /* 持久化 */
    languageDAO.get().save(language);
  }

  /**
   * 删除语言
   *
   * @param id 语言ID
   */
  void deleteLanguage(String id) {
    languageDAO.get().deleteById(id);
  }

  /**
   * 新增国际化内容
   *
   * @param i18nMessage 国际化内容
   * @throws RepeatDataException 重复数据异常
   */
  void addI18nMessage(I18nMessage i18nMessage) {
    /* 前置校验 */
    this.checkRepeat(i18nMessage);
    /* 持久化 */
    i18nMessageDAO.get().save(i18nMessage);
  }

  /**
   * 列举国际化内容命名空间
   *
   * @return 国际化内容命名空间列表
   */
  List<String> listI18nNamespace() {
    return i18nMessageDAO.get().findDistinctNamespace()
        .stream()
        .sorted()
        .toList();
  }

  /**
   * 分页查询国际化内容
   *
   * @param namespace 命名空间
   * @param odata 分页查询参数
   * @return 国际化内容列表
   */
  Page<I18nMessage> queryI18nMessageList(String namespace, ODataQueryOption odata) {
    return i18nMessageDAO.findAllWithNamespace(namespace, odata);
  }

  /**
   * 更新国际化内容
   *
   * @param i18nMessage 国际化内容
   * @throws RepeatDataException 重复数据异常
   * @throws jakarta.persistence.EntityNotFoundException 数据不存在或已删除
   */
  void updateI18nMessage(I18nMessage i18nMessage) {
    I18nMessage entity = i18nMessageDAO.get().getReferenceById(i18nMessage.getId());
    i18nMessageDAO.getCopier().copy(i18nMessage, entity);
    /* 前置校验 */
    this.checkRepeat(i18nMessage);
    /* 持久化 */
    i18nMessageDAO.get().save(i18nMessage);
  }

  /**
   * 删除国际化内容
   *
   * @param id 国际化内容ID
   */
  void deleteI18nMessage(String id) {
    i18nMessageDAO.get().deleteById(id);
  }

  /**
   * 查询国际化文本
   *
   * @param namespace 命名空间
   * @param messageKey 国际化内容
   * @return 语言-国际化文本
   */
  Map<String, String> queryI18nText(String namespace, String messageKey) {
    return i18nTextDAO.get().findByNamespaceAndMessageKey(namespace, messageKey)
        .stream()
        .collect(Collectors.toMap(I18nText::getLanguage, I18nText::getContent, (a, b) -> a));
  }

  /**
   * 新增国际化文本，如果已存在则更新
   * @param i18nText 国际化文本
   * @throws EntityNotFoundException 数据不存在或已删除
   */
  void upsertI18nText(I18nText i18nText) {
    /* 前置校验 */
    final String languageTag = i18nText.getLanguage();
    Language language = languageDAO.findByLanguageTag(languageTag)
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("Language %s not found", languageTag)
        ));
    final String namespace = i18nText.getNamespace();
    final String messageKey = i18nText.getMessageKey();
    I18nMessage i18nMessage = i18nMessageDAO.get()
        .findByNamespaceAndMessageKey(namespace, messageKey)
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("I18nMessage `%s`.`%s` not found", namespace, messageKey)
        ));
    /* 持久化 */
    Optional<I18nText> entity = i18nTextRepository.findByNamespaceAndMessageKeyAndLanguage(
        i18nMessage.getNamespace(), i18nMessage.getMessageKey(), language.getLanguageTag()
    );
    entity.ifPresent(nText -> i18nText.setId(nText.getId()));
    i18nTextDAO.get().save(i18nText);
  }

  /**
   * 加载国际化资源
   * @param language 语言标签
   * @param namespace 命名空间
   * @return 国际化内容-国际化文本
   */
  Map<String, String> loadI18nText(String language, String namespace) {
    return i18nTextDAO.get().findByNamespaceAndLanguage(namespace, language)
        .stream()
        .collect(Collectors.toMap(I18nText::getMessageKey, I18nText::getContent, (a, b) -> a));
  }

  /**
   * 语言之间不能有相同的 Language Tag
   */
  private void checkRepeat(Language language) {
    Optional<Language> exist = languageDAO.findByLanguageTag(language.getLanguageTag());
    if (exist.isPresent() && !exist.get().getId().equals(language.getId())) {
      throw new RepeatDataException("`" + language.getLanguageTag() + "`");
    }
  }

  /**
   * 同一命名空间下，国际化内容不能重复
   *
   * @param i18nMessage 国际化内容
   */
  private void checkRepeat(I18nMessage i18nMessage) {
    String namespace = i18nMessage.getNamespace();
    String messageKey = i18nMessage.getMessageKey();
    Optional<I18nMessage> exist = i18nMessageDAO.get()
        .findByNamespaceAndMessageKey(namespace, messageKey);
    if (exist.isPresent() && !exist.get().getId().equals(i18nMessage.getId())) {
      throw new RepeatDataException(String.format("`%s`.`%s`", namespace, messageKey));
    }
  }
}
