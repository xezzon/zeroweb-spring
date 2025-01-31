package io.github.xezzon.zeroweb.common.exception;

import static io.github.xezzon.zeroweb.core.error.ErrorSourceType.AUTHORIZATION;
import static io.github.xezzon.zeroweb.core.error.ErrorSourceType.CLIENT;
import static io.github.xezzon.zeroweb.core.error.ErrorSourceType.SERVER;

import cn.dev33.satoken.exception.NotLoginException;
import io.github.xezzon.zeroweb.core.error.ErrorSourceType;
import io.github.xezzon.zeroweb.core.error.IErrorCode;
import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 错误码分配中心
 * @author xezzon
 */
public enum CommonErrorCode implements IErrorCode {

  UNKNOWN(SERVER, Throwable.class),
  /**
   * 唯一键冲突
   * 消息参数0: 重复的键名
   */
  REPEAT_DATA(CLIENT, RepeatDataException.class),
  /**
   * HTTP请求参数不符合校验规则
   */
  ARGUMENT_NOT_VALID(CLIENT, MethodArgumentNotValidException.class),
  /**
   * 数据不存在或已删除
   */
  NO_SUCH_DATA(CLIENT, EntityNotFoundException.class),
  /**
   * 资源不存在
   */
  NOT_FOUND(CLIENT, NoResourceFoundException.class),
  /**
   * 未登录
   */
  NOT_LOGIN(CLIENT, NotLoginException.class),
  /**
   * 无效的token
   */
  INVALID_TOKEN(SERVER, InvalidTokenException.class),
  /**
   * 数据权限无效
   */
  DATA_PERMISSION_FORBIDDEN(AUTHORIZATION, DataPermissionForbiddenException.class),
  ;

  /**
   * 错误来源类型
   */
  private final ErrorSourceType sourceType;
  /**
   * 错误码对应的异常类
   */
  private final Class<? extends Throwable> mappedException;

  CommonErrorCode(ErrorSourceType sourceType, Class<? extends Throwable> mappedException) {
    this.sourceType = sourceType;
    this.mappedException = mappedException;
  }

  public static IErrorCode mapping(Class<? extends Throwable> exceptionClass) {
    return Arrays.stream(CommonErrorCode.values())
        .filter(o -> o.mappedException == exceptionClass)
        .findAny()
        .orElse(null);
  }

  @Override
  public ErrorSourceType sourceType() {
    return this.sourceType;
  }

  @Override
  public byte moduleCode() {
    return 0;
  }
}
