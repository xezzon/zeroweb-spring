package io.github.xezzon.zeroweb.app;

import io.github.xezzon.zeroweb.app.domain.App;
import io.github.xezzon.zeroweb.app.repository.AppRepository;
import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public class AppDAO extends BaseDAO<App, String, AppRepository> {

  protected AppDAO(final AppRepository repository) {
    super(repository, App.class);
  }

  @Override
  public ICopier<App> getCopier() {
    return Copier.INSTANCE;
  }

  @Mapper
  interface Copier extends ICopier<App> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
