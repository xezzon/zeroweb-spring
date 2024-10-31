package io.github.xezzon.geom.openapi;

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
}
