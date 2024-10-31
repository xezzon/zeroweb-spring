package io.github.xezzon.geom.openapi;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xezzon
 */
@RestController
@RequestMapping("/openapi")
public class OpenapiController {

  private final OpenapiService openapiService;

  public OpenapiController(OpenapiService openapiService) {
    this.openapiService = openapiService;
  }
}
