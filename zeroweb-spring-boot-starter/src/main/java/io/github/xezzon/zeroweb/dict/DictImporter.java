package io.github.xezzon.zeroweb.dict;

/**
 * 字典导入功能
 * @author xezzon
 */
public interface DictImporter {

  /**
   * 批量导入字典
   * 逐个判断。如果字典已存在则跳过，否则新增
   * @param reqList 字典列表
   */
  void importDict(DictImportReqList reqList);
}
