package io.github.xezzon.geom.openapi;

import io.github.xezzon.geom.common.exception.RepeatDataException;
import io.github.xezzon.geom.core.odata.ODataRequestParam;
import io.github.xezzon.geom.openapi.domain.Openapi;
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

  private void checkRepeat(Openapi openapi) {
    Openapi exist = openapiDAO.get().findByCode(openapi.getCode());
    if (exist != null) {
      throw new RepeatDataException("接口已存在");
    }
  }
}
