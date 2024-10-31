package io.github.xezzon.geom.openapi;

import io.github.xezzon.geom.common.domain.Id;
import io.github.xezzon.geom.openapi.domain.AddOpenapiReq;
import io.github.xezzon.geom.openapi.domain.Openapi;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

  /**
   * 新增`对外接口`
   * @param req 包含添加`对外接口`请求数据的请求体
   * @return 添加的`对外接口`的唯一标识符
   */
  @PostMapping("/add")
  public Id addOpenapi(@RequestBody @Valid AddOpenapiReq req) {
    Openapi openapi = req.into();
    openapiService.addOpenapi(openapi);
    return Id.of(openapi.getId());
  }
}
