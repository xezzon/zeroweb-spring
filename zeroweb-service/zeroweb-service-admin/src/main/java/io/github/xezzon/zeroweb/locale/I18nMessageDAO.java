package io.github.xezzon.zeroweb.locale;

import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import io.github.xezzon.zeroweb.locale.domain.I18nMessage;
import io.github.xezzon.zeroweb.locale.repository.I18nMessageRepository;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public class I18nMessageDAO extends BaseDAO<I18nMessage, String, I18nMessageRepository> {

  protected I18nMessageDAO(I18nMessageRepository repository) {
    super(repository, I18nMessage.class);
  }

  @Override
  public ICopier<I18nMessage> getCopier() {
    return Copier.INSTANCE;
  }

  @Mapper
  interface Copier extends ICopier<I18nMessage> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
