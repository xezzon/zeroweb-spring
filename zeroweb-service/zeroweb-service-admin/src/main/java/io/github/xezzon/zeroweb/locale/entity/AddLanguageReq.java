package io.github.xezzon.zeroweb.locale.entity;

import io.github.xezzon.tao.trait.From;
import io.github.xezzon.tao.trait.Into;
import io.github.xezzon.zeroweb.locale.domain.Language;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @param languageTag 语言标签
 * @param description 语言描述
 * @param ordinal 排序
 * @param enabled 是否启用
 * @author xezzon
 */
public record AddLanguageReq(
    String languageTag,
    String description,
    Integer ordinal,
    Boolean enabled
) implements Into<Language> {

  @Override
  public Language into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<AddLanguageReq, Language> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", defaultValue = "true")
    @Override
    Language from(AddLanguageReq source);
  }
}
