package io.github.xezzon.zeroweb.common.grpc;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant;
import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.Status;
import io.grpc.StatusException;
import io.opentelemetry.api.trace.Span;
import jakarta.persistence.EntityNotFoundException;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * @author xezzon
 */
@Component
@Slf4j
public class GrpcServerExceptionHandler implements GrpcExceptionHandler {

  public static final Key<String> ERROR_CODE =
      Key.of("X-Error-Code" + Metadata.BINARY_HEADER_SUFFIX, new Utf8Marshaller());

  @Override
  public StatusException handleException(final Throwable throwable) {
    log.warn("Request processing failed.", throwable);
    Span.current().recordException(throwable);

    return switch (throwable) {
      case ZerowebBusinessException e -> newStatusException(Status.INVALID_ARGUMENT, e.getCode());
      case MethodArgumentNotValidException _ ->
          newStatusException(Status.INVALID_ARGUMENT, ErrorCodeConstant.ARGUMENT_INVALID);
      case NotLoginException _ ->
          newStatusException(Status.UNAUTHENTICATED, ErrorCodeConstant.UNAUTHENTICATED);
      case NotRoleException _, NotPermissionException _ ->
          newStatusException(Status.PERMISSION_DENIED, ErrorCodeConstant.UNAUTHORIZED);
      case EntityNotFoundException _, NoSuchElementException _ ->
          newStatusException(Status.INVALID_ARGUMENT, ErrorCodeConstant.NO_SUCH_DATA);
      default -> newStatusException(Status.UNKNOWN, ErrorCodeConstant.UNKNOWN);
    };
  }

  private StatusException newStatusException(final Status status, final String errorCode) {
    Metadata metadata = new Metadata();
    metadata.put(ERROR_CODE, errorCode);
    return new StatusException(status, metadata);
  }
}
