package io.github.xezzon.zeroweb.common.exception;

import io.github.xezzon.zeroweb.core.error.IErrorCode;
import java.util.Optional;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author xezzon
 */
@RestControllerAdvice
public class OpenExceptionHandler extends GlobalExceptionHandler {

  @Override
  protected IErrorCode getErrorCode(Throwable e) {
    return Optional.of(e.getClass())
        .map(OpenErrorCode::mapping)
        .orElseGet(() -> super.getErrorCode(e));
  }
}
