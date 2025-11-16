package io.github.xezzon.zeroweb.setting.entity;

import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.core.trait.Into;
import io.github.xezzon.zeroweb.setting.Setting;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// 新增业务参数
/// @param key 业务参数标识
/// @param schema 约束
/// @param value 业务参数的值
/// @author xezzon
public record AddSettingRequest(
    String key,
    String schema,
    Map<String, Object> value
) implements Into<Setting> {

  @Override
  public Setting into() {
    return Converter.INSTANCE.from(this);
  }

  public

  @Mapper
  interface Converter extends From<AddSettingRequest, Setting> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Override
    Setting from(AddSettingRequest request);
  }
}
