package io.github.xezzon.geom.core.error;

/**
 * API异常响应体
 * @param code 错误码标识，放入响应头
 * @param error 错误详情
 * @author xezzon
 */
public record ErrorResponse(
    String code,
    ErrorDetail error
) {

}
