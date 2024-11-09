package io.github.xezzon.geom.openapi;

import io.github.xezzon.geom.common.domain.Id;
import io.github.xezzon.geom.core.odata.ODataRequestParam;
import io.github.xezzon.geom.openapi.domain.AddOpenapiReq;
import io.github.xezzon.geom.openapi.domain.ModifyOpenapiReq;
import io.github.xezzon.geom.openapi.domain.Openapi;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

  /**
   * 获取`对外接口`列表的分页数据
   * @param odata OData查询参数，用于分页和排序
   * @return 包含`对外接口`列表的分页对象
   */
  @GetMapping()
  public Page<Openapi> getOpenapiList(ODataRequestParam odata) {
    return openapiService.pageList(odata);
  }

  /**
   * 更新`对外接口`信息
   * @param req 包含更新`对外接口`请求数据的请求体
   */
  @PutMapping("/update")
  public void modifyDict(@RequestBody ModifyOpenapiReq req) {
    Openapi openapi = req.into();
    openapiService.modifyOpenapi(openapi);
  }
}