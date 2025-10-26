package io.github.xezzon.zeroweb.common.concurrent;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author xezzon
 */
public interface LockAdaptor {

  /// 加锁执行操作
  /// @param id 资源ID
  /// @param supplier 要执行的操作
  /// @return 操作返回值，如果获取锁失败则返回 empty
  /// @param <R> 操作返回类型
  <R> Optional<R> tryLock(String id, Supplier<R> supplier);
}
