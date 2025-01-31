package io.github.xezzon.zeroweb.common.exception;

import static io.github.xezzon.zeroweb.core.error.ErrorSourceType.CLIENT;

import io.github.xezzon.zeroweb.core.error.ErrorSourceType;
import io.github.xezzon.zeroweb.core.error.IErrorCode;
import java.util.Arrays;

/**
 * 错误码分配中心
 * @author xezzon
 */
public enum AdminErrorCode implements IErrorCode {

  /**
   * 用户名或密码错误
   */
  INVALID_PASSWORD(CLIENT, InvalidPasswordException.class),
  ;

  /**
   * 错误来源类型
   */
  private final ErrorSourceType sourceType;
  /**
   * 错误码对应的异常类
   */
  private final Class<? extends Throwable> mappedException;

  public static IErrorCode mapping(Class<? extends Throwable> exceptionClass) {
    return Arrays.stream(AdminErrorCode.values())
        .filter(o -> o.mappedException == exceptionClass)
        .findAny()
        .orElse(null);
  }

  AdminErrorCode(ErrorSourceType sourceType, Class<? extends Throwable> mappedException) {
    this.sourceType = sourceType;
    this.mappedException = mappedException;
  }

  @Override
  public ErrorSourceType sourceType() {
    return this.sourceType;
  }

  @Override
  public byte moduleCode() {
    return -1;
  }
}
