package io.github.xezzon.geom.dict;

import io.github.xezzon.geom.common.constant.DatabaseConstant;
import io.github.xezzon.geom.common.domain.Id;
import io.github.xezzon.geom.dict.domain.AddDictReq;
import io.github.xezzon.geom.dict.domain.Dict;
import io.github.xezzon.geom.dict.domain.ModifyDictReq;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 字典管理
 * @author xezzon
 */
@RestController
@RequestMapping("/dict")
public class DictController {

  private final DictService dictService;

  public DictController(DictService dictService) {
    this.dictService = dictService;
  }

  /**
   * 新增字典目/字典项
   * @param req 对于字典项，字典目、上级ID不能为空
   * @return 字典ID
   */
  @PostMapping("/add")
  public Id addDict(@RequestBody AddDictReq req) {
    Dict dict = req.into();
    if (dict.getTag() == null || dict.getParentId() == null) {
      // 新增字典目时，由后端设置以下属性的值
      dict.setTag(Dict.DICT_TAG);
      dict.setParentId(DatabaseConstant.ROOT_ID);
    }
    dictService.addDict(dict);
    return Id.of(dict.getId());
  }

  /**
   * 更新字典目/字典项
   * @param req 字典
   */
  @PutMapping("/update")
  public void modifyDict(@RequestBody ModifyDictReq req) {
    Dict dict = req.into();
    dictService.modifyDict(dict);
  }
}
