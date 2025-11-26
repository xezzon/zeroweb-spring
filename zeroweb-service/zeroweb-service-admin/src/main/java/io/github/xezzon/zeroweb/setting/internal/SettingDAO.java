package io.github.xezzon.zeroweb.setting.internal;

import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.setting.Setting;
import io.github.xezzon.zeroweb.setting.Setting_;
import io.github.xezzon.zeroweb.setting.repository.SettingRepository;
import org.jspecify.annotations.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public class SettingDAO extends BaseDAO<Setting, String, SettingRepository> {

  SettingDAO(final SettingRepository repository) {
    super(repository, Setting.class);
  }

  @Override
  public Page<@NonNull Setting> findAll(final @NonNull ODataQueryOption odata) {
    Sort sort = Sort.by(Order.desc(Setting_.UPDATE_TIME));
    return super.findAll(odata, null, sort);
  }

  @Override
  public ICopier<Setting> getCopier() {
    return Copier.INSTANCE;
  }

  @Mapper
  interface Copier extends ICopier<Setting> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
