package io.github.xezzon.zeroweb.app.entity;

import io.github.xezzon.tao.trait.From;
import io.github.xezzon.tao.trait.Into;
import io.github.xezzon.zeroweb.app.App;
import org.hibernate.validator.constraints.URL;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// 新增服务
///
/// @param name 服务名称
/// @param baseUrl 基础访问路径
/// @param ordinal 排序值 顺序越小越靠前
/// @author xezzon
public record AddAppReq(
    String name,
    @URL
    String baseUrl,
    Integer ordinal
) implements Into<App> {

  @Override
  public App into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<AddAppReq, App> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "id", ignore = true)
    @Override
    App from(AddAppReq source);
  }
}
