package io.github.xezzon.zeroweb.setting.entity;

/// 更新业务参数请求（仅更新值）
/// @param id 需要更新的业务参数的ID
/// @param value 业务参数的值
/// @author xezzon
public record UpdateValueRequest(
    String id,
    String value
) {

}
