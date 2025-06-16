package io.github.xezzon.zeroweb.common.exception;

import io.github.xezzon.zeroweb.core.error.IErrorCode;
import java.util.Map;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * ZeroWeb 业务异常
 * @author xezzon
 */
public abstract class ZerowebBusinessException extends RuntimeException {

  @Getter
  private final IErrorCode errorCode;
  @Getter
  private final Map<String, Object> parameters;

  protected ZerowebBusinessException(
      @NotNull IErrorCode errorCode,
      @NotNull Map<String, Object> parameters,
      String message
  ) {
    super(message);
    this.errorCode = errorCode;
    this.parameters = parameters;
  }
}
