package io.github.xezzon.geom.dict;

import io.github.xezzon.geom.common.constant.DatabaseConstant;
import io.github.xezzon.geom.common.domain.Id;
import io.github.xezzon.geom.dict.domain.AddDictReq;
import io.github.xezzon.geom.dict.domain.Dict;
import io.github.xezzon.geom.dict.domain.ModifyDictReq;
import io.github.xezzon.tao.tree.Tree;
import java.util.Collection;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
   * 查询指定字典目下所有字典项的列表
   * @param tag 字典目编码
   * @return 字典项列表（树形结构）
   */
  @GetMapping("/tag/{tag}")
  public List<Dict> getDictTreeByTag(@PathVariable String tag) {
    List<Dict> dictItemList = dictService.getDictItemList(tag);
    return Tree.fold(dictItemList);
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

  /**
   * 批量更新字典状态
   * @param ids 字典ID集合
   * @param enabled 更新后的字典启用状态
   */
  @PutMapping("/update/status")
  public void updateDictStatus(
      @RequestBody Collection<String> ids,
      @RequestParam Boolean enabled
  ) {
    dictService.updateDictStatus(ids, enabled);
  }

  /**
   * 批量删除字典目/字典项
   * @param ids 字典ID集合
   */
  @DeleteMapping()
  public void removeDict(@RequestBody Collection<String> ids) {
    dictService.remove(ids);
  }
}
