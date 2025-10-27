package io.github.xezzon.zeroweb.common.concurrent;

import com.google.common.util.concurrent.Striped;
import io.github.xezzon.zeroweb.common.exception.ZerowebRuntimeException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

/**
 * @author xezzon
 */
@Component
@Fallback
public class StripedLock implements LockProvider {

  @Override
  public LockAdaptor of(String name, int timeout) {
    return new InnerLock(timeout);
  }

  public static class InnerLock implements LockAdaptor {

    private final Striped<@NotNull Lock> stripedLocks = Striped.lazyWeakLock(100);
    private final int timeout;

    public InnerLock(int timeout) {
      this.timeout = timeout;
    }

    @Override
    public <R> Optional<R> tryLock(String id, Supplier<R> supplier) {
      Lock specificLock = stripedLocks.get(id);
      try {
        if (!specificLock.tryLock(timeout, TimeUnit.SECONDS)) {
          return Optional.empty();
        }
        try {
          return Optional.ofNullable(supplier.get());
        } finally {
          specificLock.unlock();
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new ZerowebRuntimeException(e);
      }
    }
  }
}
