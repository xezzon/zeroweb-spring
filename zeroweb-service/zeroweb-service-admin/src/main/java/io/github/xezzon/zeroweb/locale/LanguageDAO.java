package io.github.xezzon.zeroweb.locale;

import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import io.github.xezzon.zeroweb.locale.domain.Language;
import io.github.xezzon.zeroweb.locale.repository.LanguageRepository;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public class LanguageDAO extends BaseDAO<Language, String, LanguageRepository> {

  protected LanguageDAO(LanguageRepository repository) {
    super(repository, Language.class);
  }

  @Override
  public ICopier<Language> getCopier() {
    return Copier.INSTANCE;
  }

  Optional<Language> findByLanguageTag(String languageTag) {
    return this.get().findByDictTagAndLanguageTag(Language.LANGUAGE_DICT_TAG, languageTag);
  }

  @Mapper
  interface Copier extends ICopier<Language> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
