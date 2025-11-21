package io.github.xezzon.zeroweb.app.entity;

import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.core.trait.Into;
import io.github.xezzon.zeroweb.app.App;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/// 更新服务信息
///
/// @param id 服务ID
/// @param name 服务名称
/// @param baseUrl 服务基础访问路径
/// @param ordinal 排序值 顺序越小越靠前
/// @author xezzon
public record UpdateAppReq(
    String id,
    @NotNull
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
  interface Converter extends From<UpdateAppReq, App> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Override
    App from(UpdateAppReq source);
  }
}
