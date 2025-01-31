package io.github.xezzon.zeroweb.locale.entity;

import io.github.xezzon.tao.trait.From;
import io.github.xezzon.tao.trait.Into;
import io.github.xezzon.zeroweb.locale.domain.Translation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 更新或新增国际化文本的请求体
 * @param namespace 命名空间 与国际化内容的命名空间一致
 * @param messageKey 国际化内容
 * @param language 国际化语言标签
 * @param content 国际化文本
 * @author xezzon
 */
public record UpsertTranslationReq(
    String namespace,
    String messageKey,
    String language,
    String content
) implements Into<Translation> {

  @Override
  public Translation into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<UpsertTranslationReq, Translation> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "id", ignore = true)
    @Override
    Translation from(UpsertTranslationReq source);
  }
}
