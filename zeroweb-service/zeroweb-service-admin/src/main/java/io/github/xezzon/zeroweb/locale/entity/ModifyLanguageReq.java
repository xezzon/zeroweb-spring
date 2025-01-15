package io.github.xezzon.zeroweb.locale.entity;

import io.github.xezzon.tao.trait.From;
import io.github.xezzon.tao.trait.Into;
import io.github.xezzon.zeroweb.locale.domain.Language;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 修改语言的请求参数
 * @param languageTag 语言标签
 * @param description 语言描述
 * @param ordinal 排序序号
 * @param enabled 是否启用
 * @author xezzon
 */
public record ModifyLanguageReq(
    String id,
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
  interface Converter extends From<ModifyLanguageReq, Language> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Override
    Language from(ModifyLanguageReq source);
  }
}
