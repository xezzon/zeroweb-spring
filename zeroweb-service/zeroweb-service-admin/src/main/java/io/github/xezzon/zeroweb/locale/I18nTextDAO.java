package io.github.xezzon.zeroweb.locale;

import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import io.github.xezzon.zeroweb.locale.domain.I18nText;
import io.github.xezzon.zeroweb.locale.repository.I18nTextRepository;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;

@Repository
public class I18nTextDAO extends BaseDAO<I18nText, String, I18nTextRepository> {

  protected I18nTextDAO(I18nTextRepository repository) {
    super(repository, I18nText.class);
  }

  @Override
  public ICopier<I18nText> getCopier() {
    return Copier.INSTANCE;
  }

  @Mapper
  interface Copier extends ICopier<I18nText> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
