package io.github.xezzon.zeroweb.dict.entity;

import io.github.xezzon.tao.trait.From;
import io.github.xezzon.tao.trait.Into;
import io.github.xezzon.zeroweb.dict.domain.Dict;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author xezzon
 */
@Getter
@Setter
public class AddDictReq implements Into<Dict> {

  /**
   * 字典目
   */
  @Nullable
  private String tag;
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
  @Nullable
  private String parentId;

  public Dict into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<AddDictReq, Dict> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "editable", constant = "true")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Override
    Dict from(AddDictReq source);
  }
}
