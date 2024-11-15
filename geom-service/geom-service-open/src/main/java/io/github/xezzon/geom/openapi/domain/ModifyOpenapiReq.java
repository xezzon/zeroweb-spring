package io.github.xezzon.geom.openapi.domain;

import io.github.xezzon.geom.common.validator.Alphanumeric;
import io.github.xezzon.tao.trait.From;
import io.github.xezzon.tao.trait.Into;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @param id 对外接口标识
 * @param code 接口编码
 * @author xezzon
 */
public record ModifyOpenapiReq(
    String id,
    @Alphanumeric
    String code
) implements Into<Openapi> {

  @Override
  public Openapi into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<ModifyOpenapiReq, Openapi> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "status", ignore = true)
    @Override
    Openapi from(ModifyOpenapiReq source);
  }
}
