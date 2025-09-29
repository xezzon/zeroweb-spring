package io.github.xezzon.zeroweb.common.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ThreadPoolService {

  @Async(ThreadPoolConfig.CPU_INTENSIVE_EXECUTOR)
  public Future<String> asyncCpuIntensiveTask(String input) {
    return CompletableFuture.completedFuture("CPU Intensive Task Result: " + input);
  }

  @Async(ThreadPoolConfig.IO_INTENSIVE_EXECUTOR)
  public Future<Integer> asyncIoIntensiveTask(int input) {
    return CompletableFuture.completedFuture(input * 2);
  }

  @Async
  public Future<String> asyncDefaultTask(String input) {
    return CompletableFuture.completedFuture("Default Task Result: " + input);
  }
}
