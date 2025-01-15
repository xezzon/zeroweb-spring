package io.github.xezzon.zeroweb.locale;

import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.locale.domain.I18nMessage;
import io.github.xezzon.zeroweb.locale.domain.Language;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class LocalizedService {

  private final LanguageDAO languageDAO;
  private final I18nMessageDAO i18nMessageDAO;

  LocalizedService(LanguageDAO languageDAO, I18nMessageDAO i18nMessageDAO) {
    this.languageDAO = languageDAO;
    this.i18nMessageDAO = i18nMessageDAO;
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
   * @param id 语言ID
   */
  void deleteLanguage(String id) {
    languageDAO.get().deleteById(id);
  }

  /**
   * 新增国际化内容
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
   * @return 国际化内容命名空间列表
   */
  List<String> listI18nNamespace() {
    return i18nMessageDAO.get().findDistinctNamespace()
        .stream()
        .sorted()
        .toList();
  }

  /**
   * 更新国际化内容
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
   * @param id 国际化内容ID
   */
  void deleteI18nMessage(String id) {
    i18nMessageDAO.get().deleteById(id);
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
