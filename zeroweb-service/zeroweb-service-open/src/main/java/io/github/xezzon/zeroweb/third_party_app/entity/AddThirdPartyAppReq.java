package io.github.xezzon.zeroweb.third_party_app.entity;

import io.github.xezzon.tao.trait.From;
import io.github.xezzon.tao.trait.Into;
import io.github.xezzon.zeroweb.third_party_app.domain.ThirdPartyApp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @param name 第三方应用名称
 * @author xezzon
 */
public record AddThirdPartyAppReq(
    String name
) implements Into<ThirdPartyApp> {

  @Override
  public ThirdPartyApp into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<AddThirdPartyAppReq, ThirdPartyApp> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Override
    ThirdPartyApp from(AddThirdPartyAppReq source);
  }
}
