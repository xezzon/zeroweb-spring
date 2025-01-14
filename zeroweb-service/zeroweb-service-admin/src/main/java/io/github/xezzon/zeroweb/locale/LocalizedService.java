package io.github.xezzon.zeroweb.locale;

import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.locale.domain.Language;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class LocalizedService {

  private final LanguageDAO languageDAO;

  LocalizedService(LanguageDAO languageDAO) {
    this.languageDAO = languageDAO;
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
   * 语言之间不能有相同的 Language Tag
   */
  private void checkRepeat(Language language) {
    Optional<Language> exist = languageDAO.findByLanguageTag(language.getLanguageTag());
    if (exist.isPresent() && !exist.get().getId().equals(language.getId())) {
      throw new RepeatDataException("`" + language.getLanguageTag() + "`");
    }
  }
}
