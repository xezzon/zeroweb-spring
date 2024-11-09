package io.github.xezzon.geom.openapi;

import io.github.xezzon.geom.common.PublishedOpenapiCannotBeModifyException;
import io.github.xezzon.geom.common.exception.RepeatDataException;
import io.github.xezzon.geom.core.odata.ODataRequestParam;
import io.github.xezzon.geom.openapi.domain.Openapi;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class OpenapiService {

  private final OpenapiDAO openapiDAO;

  public OpenapiService(OpenapiDAO openapiDAO) {
    this.openapiDAO = openapiDAO;
  }

  protected void addOpenapi(Openapi openapi) {
    this.checkRepeat(openapi);
    openapiDAO.get().save(openapi);
  }

  protected Page<Openapi> pageList(ODataRequestParam odata) {
    return openapiDAO.findAll(odata.into());
  }

  protected void modifyOpenapi(Openapi openapi) {
    this.checkRepeat(openapi);
    Openapi entity = openapiDAO.get().getReferenceById(openapi.getId());
    if (entity.isPublished()) {
      throw new PublishedOpenapiCannotBeModifyException();
    }
    openapiDAO.partialUpdate(openapi);
  }

  private void checkRepeat(Openapi openapi) {
    Openapi exist = openapiDAO.get().findByCode(openapi.getCode());
    if (exist != null && !Objects.equals(exist.getId(), openapi.getId())) {
      throw new RepeatDataException("接口已存在");
    }
  }
}
