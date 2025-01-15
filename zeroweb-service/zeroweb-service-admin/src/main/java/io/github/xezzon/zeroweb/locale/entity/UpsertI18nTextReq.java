package io.github.xezzon.zeroweb.locale.entity;

import io.github.xezzon.tao.trait.From;
import io.github.xezzon.tao.trait.Into;
import io.github.xezzon.zeroweb.locale.domain.I18nText;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public record UpsertI18nTextReq(
    String namespace,
    String messageKey,
    String language,
    String content
) implements Into<I18nText> {

  @Override
  public I18nText into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<UpsertI18nTextReq, I18nText> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "id", ignore = true)
    @Override
    I18nText from(UpsertI18nTextReq source);
  }
}
