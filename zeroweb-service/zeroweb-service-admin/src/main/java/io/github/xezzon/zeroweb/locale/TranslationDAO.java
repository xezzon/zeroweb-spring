package io.github.xezzon.zeroweb.locale;

import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import io.github.xezzon.zeroweb.locale.domain.Translation;
import io.github.xezzon.zeroweb.locale.repository.TranslationRepository;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;

@Repository
public class TranslationDAO extends BaseDAO<Translation, String, TranslationRepository> {

  protected TranslationDAO(final TranslationRepository repository) {
    super(repository, Translation.class);
  }

  @Override
  public ICopier<Translation> getCopier() {
    return Copier.INSTANCE;
  }

  @Mapper
  interface Copier extends ICopier<Translation> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
