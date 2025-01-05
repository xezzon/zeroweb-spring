package io.github.xezzon.zeroweb.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 找不到有效的类路径
 * @author xezzon
 */
@Slf4j
public class NoValidClasspathException extends ZerowebRuntimeException {

  public NoValidClasspathException(Throwable cause) {
    super("No valid classpath found", cause);
  }
}
