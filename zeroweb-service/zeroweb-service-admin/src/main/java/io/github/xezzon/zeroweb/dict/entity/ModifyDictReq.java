package io.github.xezzon.zeroweb.dict.entity;

import io.github.xezzon.tao.trait.From;
import io.github.xezzon.tao.trait.Into;
import io.github.xezzon.zeroweb.dict.Dict;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// @param code 字典键
/// @param label 字典值
/// @param ordinal 排序号
/// @param parentId 上级字典ID
/// @param enabled 启用状态
public record ModifyDictReq(
    String id,
    String code,
    String label,
    Integer ordinal,
    String parentId,
    Boolean enabled
) implements Into<Dict> {

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
