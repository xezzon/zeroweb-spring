package io.github.xezzon.zeroweb.common.exception;

import java.util.Collections;
import java.util.Map;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * ZeroWeb 业务异常
 * @author xezzon
 */
public abstract class ZerowebBusinessException extends RuntimeException {

  @Getter
  private final transient Map<String, Object> parameters;

  protected ZerowebBusinessException(String message) {
    super(message);
    this.parameters = Collections.emptyMap();
  }

  protected ZerowebBusinessException(
      @NotNull Map<String, Object> parameters,
      String message
  ) {
    super(message);
    this.parameters = parameters;
  }

  public abstract String getCode();
}
