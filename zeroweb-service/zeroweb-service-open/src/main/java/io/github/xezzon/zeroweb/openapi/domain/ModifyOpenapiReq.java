package io.github.xezzon.zeroweb.openapi.domain;

import io.github.xezzon.tao.trait.From;
import io.github.xezzon.tao.trait.Into;
import io.github.xezzon.zeroweb.common.validator.Alphanumeric;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @param id 对外接口标识
 * @param code 接口编码
 * @param destination 后端地址
 * @param httpMethod 请求接口的HTTP方法
 * @author xezzon
 */
public record ModifyOpenapiReq(
    String id,
    @Alphanumeric String code,
    String destination,
    HttpMethod httpMethod
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
