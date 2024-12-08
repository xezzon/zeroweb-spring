package io.github.xezzon.zeroweb.openapi;

import io.github.xezzon.zeroweb.common.exception.PublishedOpenapiCannotBeModifyException;
import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.openapi.domain.Openapi;
import io.github.xezzon.zeroweb.openapi.domain.OpenapiStatus;
import io.github.xezzon.zeroweb.openapi.service.IOpenapiService4Subscription;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class OpenapiService implements IOpenapiService4Subscription {

  private final OpenapiDAO openapiDAO;

  public OpenapiService(OpenapiDAO openapiDAO) {
    this.openapiDAO = openapiDAO;
  }

  protected void addOpenapi(Openapi openapi) {
    this.checkRepeat(openapi);
    openapiDAO.get().save(openapi);
  }

  protected Page<Openapi> pageList(ODataQueryOption odata) {
    return openapiDAO.findAll(odata);
  }

  protected void modifyOpenapi(Openapi openapi) {
    this.checkRepeat(openapi);
    Openapi entity = openapiDAO.get().getReferenceById(openapi.getId());
    if (entity.isPublished()) {
      throw new PublishedOpenapiCannotBeModifyException();
    }
    openapiDAO.partialUpdate(openapi);
  }

  protected void publishOpenapi(String id) {
    Openapi entity = openapiDAO.get().getReferenceById(id);
    entity.setStatus(OpenapiStatus.PUBLISHED);
    openapiDAO.get().save(entity);
  }

  private void checkRepeat(Openapi openapi) {
    Openapi exist = openapiDAO.get().findByCode(openapi.getCode());
    if (exist != null && !Objects.equals(exist.getId(), openapi.getId())) {
      throw new RepeatDataException("接口已存在");
    }
  }

  @Override
  public Page<Openapi> listPublishedOpenapi(ODataQueryOption odata) {
    return openapiDAO.listPublishedOpenapi(odata);
  }

  @Override
  public @Nullable Openapi getByCode(String openapiCode) {
    return openapiDAO.get().findByCode(openapiCode);
  }
}
