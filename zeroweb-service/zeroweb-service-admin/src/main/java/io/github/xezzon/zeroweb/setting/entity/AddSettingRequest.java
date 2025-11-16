package io.github.xezzon.zeroweb.setting.entity;

/// 新增业务参数
/// @param key 业务参数标识
/// @param schema 约束
/// @param value 业务参数的值
/// @author xezzon
public record AddSettingRequest(
    String key,
    String schema,
    String value
) {

}
