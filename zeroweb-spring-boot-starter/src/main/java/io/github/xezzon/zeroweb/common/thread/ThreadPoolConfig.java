package io.github.xezzon.zeroweb.common.thread;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;

/// 线程池配置
/// @author xezzon
@Configuration
@ConfigurationProperties(prefix = "thread-pool")
@EnableAsync
public class ThreadPoolConfig {

  public static final String CPU_INTENSIVE_EXECUTOR = "cpuIntensiveExecutor";
  public static final String IO_INTENSIVE_EXECUTOR = "ioIntensiveExecutor";

  @Bean(CPU_INTENSIVE_EXECUTOR)
  Executor cpuIntensiveExecutor() {
    return new ForkJoinPool(
        Runtime.getRuntime().availableProcessors(),
        pool -> {
          ForkJoinWorkerThread worker = ForkJoinPool
              .defaultForkJoinWorkerThreadFactory
              .newThread(pool);
          worker.setName("cpu-intensive-task-" + worker.getPoolIndex());
          return worker;
        },
        (_, _) -> {
        },
        false
    );
  }

  @Bean(IO_INTENSIVE_EXECUTOR)
  @Primary
  Executor ioIntensiveExecutor() {
    return Executors.newThreadPerTaskExecutor(
        Thread.ofVirtual().name("io-intensive-task-", 0).factory()
    );
  }
}
