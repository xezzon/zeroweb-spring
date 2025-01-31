package io.github.xezzon.zeroweb.locale.entity;

import io.github.xezzon.tao.trait.From;
import io.github.xezzon.tao.trait.Into;
import io.github.xezzon.zeroweb.locale.domain.I18nMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 新增国际化内容的请求参数
 * @author xezzon
 */
public record AddI18nMessageReq(
    String namespace,
    String messageKey
) implements Into<I18nMessage> {

  @Override
  public I18nMessage into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<AddI18nMessageReq, I18nMessage> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "id", ignore = true)
    @Override
    I18nMessage from(AddI18nMessageReq source);
  }
}
