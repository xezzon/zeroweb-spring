package io.github.xezzon.zeroweb.setting.entity;

import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.core.trait.Into;
import io.github.xezzon.zeroweb.setting.Setting;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// 更新业务参数请求（仅更新值）
/// @param id 需要更新的业务参数的ID
/// @param value 业务参数的值
/// @author xezzon
public record UpdateValueRequest(
    String id,
    Map<String, Object> value
) implements Into<Setting> {

  @Override
  public Setting into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<UpdateValueRequest, Setting> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "schema", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Override
    Setting from(UpdateValueRequest source);
  }
}
