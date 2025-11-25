package io.github.xezzon.zeroweb.common.thread;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.annotation.Resource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ThreadPoolConfigTest {

  @Resource
  private ThreadPoolService threadPoolService;

  @Test
  void testAsyncCpuIntensiveTask() throws ExecutionException, InterruptedException {
    String input = "testCpu";
    Future<String> future = threadPoolService.asyncCpuIntensiveTask(input);
    assertNotNull(future);
    String result = future.get();
    assertEquals("CPU Intensive Task Result: " + input, result);
    assertTrue(result.contains("CPU Intensive Task Result"));
  }

  @Test
  void testAsyncIoIntensiveTask() throws ExecutionException, InterruptedException {
    int input = 10;
    Future<Integer> future = threadPoolService.asyncIoIntensiveTask(input);
    assertNotNull(future);
    Integer result = future.get();
    assertEquals(input * 2, result);
  }

  @Test
  void testAsyncDefaultTask() throws ExecutionException, InterruptedException {
    String input = "testDefault";
    Future<String> future = threadPoolService.asyncDefaultTask(input);
    assertNotNull(future);
    String result = future.get();
    assertEquals("Default Task Result: " + input, result);
  }
}
