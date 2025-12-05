/*
 * SPDX-FileCopyrightText: Copyright (C) 2025 xezzon
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This file is part of ZeroWeb.
 *
 * ZeroWeb is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * ZeroWeb is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with ZeroWeb. If not, see <https://www.gnu.org/licenses/>.
 */

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

/// 线程池配置类，用于定义和配置应用程序中使用的不同类型的线程池。
/// 主要包括 CPU 密集型任务线程池和 IO 密集型任务线程池。
///
/// @author xezzon
@Configuration
@ConfigurationProperties(prefix = "thread-pool")
@EnableAsync
public class ThreadPoolConfig {

  /// CPU 密集型任务执行器 bean 的名称。
  public static final String CPU_INTENSIVE_EXECUTOR = "cpuIntensiveExecutor";
  /// IO 密集型任务执行器 bean 的名称。
  public static final String IO_INTENSIVE_EXECUTOR = "ioIntensiveExecutor";

  /// 创建并配置一个 CPU 密集型任务执行器。
  /// 该执行器使用 [ForkJoinPool]，其并行度设置为可用处理器数量，
  /// 并为工作线程设置自定义名称前缀 "cpu-intensive-task-"。
  ///
  /// @return 配置好的 CPU 密集型任务执行器。
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

  /// 创建并配置一个 IO 密集型任务执行器。
  /// 该执行器使用虚拟线程，为每个任务创建一个新线程，线程名称前缀为 "io-intensive-task-"。
  /// 被标记为 [Primary]，表示它是首选的 [Executor] 实现。
  ///
  /// @return 配置好的 IO 密集型任务执行器。
  @Bean(IO_INTENSIVE_EXECUTOR)
  @Primary
  Executor ioIntensiveExecutor() {
    return Executors.newThreadPerTaskExecutor(
        Thread.ofVirtual().name("io-intensive-task-", 0).factory()
    );
  }
}
