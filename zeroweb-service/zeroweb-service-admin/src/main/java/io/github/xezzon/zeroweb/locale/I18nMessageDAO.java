package io.github.xezzon.zeroweb.locale;

import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.locale.domain.I18nMessage;
import io.github.xezzon.zeroweb.locale.domain.I18nMessage_;
import io.github.xezzon.zeroweb.locale.repository.I18nMessageRepository;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public class I18nMessageDAO extends BaseDAO<I18nMessage, String, I18nMessageRepository> {

  protected I18nMessageDAO(final I18nMessageRepository repository) {
    super(repository, I18nMessage.class);
  }

  @Override
  public ICopier<I18nMessage> getCopier() {
    return Copier.INSTANCE;
  }

  Page<I18nMessage> findAllWithNamespace(final String namespace, final ODataQueryOption odata) {
    final Specification<I18nMessage> spec = (root, query, cb) ->
        cb.equal(root.get(I18nMessage_.namespace), namespace);
    final Sort sort = Sort.by(Order.asc(I18nMessage_.MESSAGE_KEY));
    return super.findAll(odata, spec, sort);
  }

  @Mapper
  interface Copier extends ICopier<I18nMessage> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
