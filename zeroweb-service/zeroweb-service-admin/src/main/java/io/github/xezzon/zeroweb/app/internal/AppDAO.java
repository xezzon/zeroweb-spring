package io.github.xezzon.zeroweb.app.internal;

import io.github.xezzon.zeroweb.app.App;
import io.github.xezzon.zeroweb.app.repository.AppRepository;
import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import org.jspecify.annotations.NullMarked;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;

/// @author xezzon
@Repository
@NullMarked
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
