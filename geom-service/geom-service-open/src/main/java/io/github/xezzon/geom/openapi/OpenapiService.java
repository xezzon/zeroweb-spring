package io.github.xezzon.geom.openapi;

import io.github.xezzon.geom.common.exception.RepeatDataException;
import io.github.xezzon.geom.openapi.domain.Openapi;
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

  private void checkRepeat(Openapi openapi) {
    Openapi exist = openapiDAO.get().findByCode(openapi.getCode());
    if (exist != null) {
      throw new RepeatDataException("接口已存在");
    }
  }
}
