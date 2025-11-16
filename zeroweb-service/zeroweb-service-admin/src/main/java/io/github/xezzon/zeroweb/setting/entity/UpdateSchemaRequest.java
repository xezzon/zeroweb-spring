package io.github.xezzon.zeroweb.setting.entity;

/// 更新业务参数请求
/// @param id 需要更新的业务参数的ID
/// @param schema 约束
/// @param value 业务参数的值
/// @author xezzon
public record UpdateSchemaRequest(
    String id,
    String schema,
    String value
) {

}
