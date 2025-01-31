package io.github.xezzon.zeroweb.openapi.entity;

import io.github.xezzon.tao.trait.From;
import io.github.xezzon.tao.trait.Into;
import io.github.xezzon.zeroweb.common.validator.Alphanumeric;
import io.github.xezzon.zeroweb.openapi.domain.HttpMethod;
import io.github.xezzon.zeroweb.openapi.domain.Openapi;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @param code 接口编码
 * @param destination 后端地址
 * @param httpMethod 请求接口的HTTP方法
 * @author xezzon
 */
public record AddOpenapiReq(
    @Alphanumeric(excludes = {Alphanumeric.DOT}) String code,
    String destination,
    HttpMethod httpMethod
) implements Into<Openapi> {

  @Override
  public Openapi into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<AddOpenapiReq, Openapi> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "status", constant = "DRAFT")
    @Mapping(target = "id", ignore = true)
    @Override
    Openapi from(AddOpenapiReq source);
  }
}
