package io.github.xezzon.zeroweb.common.concurrent;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * @author xezzon
 */
@SpringBootTest
@DirtiesContext
class StripedLockTest {

  private final LockAdaptor lockAdaptor;

  @Autowired
  StripedLockTest(LockProvider lockProvider) {
    this.lockAdaptor = lockProvider.of("test", 1);
  }

  @Test
  void tryLock() throws InterruptedException {
    Assertions.assertInstanceOf(StripedLock.InnerLock.class, lockAdaptor);
    AtomicInteger counter = new AtomicInteger(0);
    AtomicBoolean isLocked = new AtomicBoolean(false); // 用于验证互斥性
    int numberOfThreads = 10;
    CountDownLatch latch = new CountDownLatch(numberOfThreads);
    try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
      for (int i = 0; i < numberOfThreads; i++) {
        executorService.submit(() -> {
          try {
            lockAdaptor.tryLock("resourceId", () -> {
              // 验证互斥性：在进入临界区前，isLocked 必须为 false
              Assertions.assertFalse(isLocked.getAndSet(true), "Lock should be held by only one thread at a time");
              counter.incrementAndGet();
              try {
                Thread.sleep(50); // 模拟临界区内的操作延迟
              } catch (InterruptedException _) {
                Thread.currentThread().interrupt();
              } finally {
                isLocked.set(false); // 离开临界区，释放锁状态
              }
              return null;
            });
          } finally {
            latch.countDown();
          }
        });
      }

      latch.await(5, TimeUnit.SECONDS); // 等待所有线程完成，设置超时时间

      Assertions.assertEquals(numberOfThreads, counter.get());
      Assertions.assertFalse(isLocked.get(), "Lock should be released after all operations");
    }
  }

  @Test
  void tryLock_timeoutReturnsNull() throws InterruptedException {
    // 模拟一个线程长时间持有锁
    try (ExecutorService longHoldingExecutor = Executors.newSingleThreadExecutor()) {
      CountDownLatch lockHeldLatch = new CountDownLatch(1);
      CountDownLatch lockReleasedLatch = new CountDownLatch(1);

      longHoldingExecutor.submit(() -> {
        lockAdaptor.tryLock("timeoutResourceId", () -> {
          lockHeldLatch.countDown(); // 通知锁已被持有
          try {
            Thread.sleep(2000); // 长时间持有锁
          } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
          } finally {
            lockReleasedLatch.countDown(); // 通知锁已被释放
          }
          return null;
        });
      });

      lockHeldLatch.await(1, TimeUnit.SECONDS); // 等待第一个线程获取锁

      // 尝试在超时时间内获取同一个锁
      Optional<String> result = lockAdaptor.tryLock("timeoutResourceId", () -> "locked");

      Assertions.assertTrue(result.isEmpty(), "Expected tryLock to return null on timeout");

      lockReleasedLatch.await(3, TimeUnit.SECONDS); // 等待第一个线程释放锁
      longHoldingExecutor.shutdownNow(); // 关闭线程池
    }
  }
}
