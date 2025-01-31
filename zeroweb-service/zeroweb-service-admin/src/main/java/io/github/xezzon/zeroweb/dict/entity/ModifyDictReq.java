package io.github.xezzon.zeroweb.dict.entity;

import io.github.xezzon.tao.trait.From;
import io.github.xezzon.tao.trait.Into;
import io.github.xezzon.zeroweb.dict.domain.Dict;
import lombok.Getter;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Getter
@Setter
public class ModifyDictReq implements Into<Dict> {

  private String id;
  /**
   * 字典键
   */
  private String code;
  /**
   * 字典值
   */
  private String label;
  /**
   * 排序号
   */
  private Integer ordinal;
  /**
   * 上级字典ID
   */
  private String parentId;
  /**
   * 启用状态
   */
  private Boolean enabled;

  @Override
  public Dict into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<ModifyDictReq, Dict> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "editable", ignore = true)
    @Mapping(target = "tag", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Override
    Dict from(ModifyDictReq source);
  }
}
