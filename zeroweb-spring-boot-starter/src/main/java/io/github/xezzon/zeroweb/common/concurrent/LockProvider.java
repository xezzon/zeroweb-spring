package io.github.xezzon.zeroweb.common.concurrent;

/**
 * @author xezzon
 */
public interface LockProvider {

  /// 获取锁封装器
  /// @param name 业务类型
  /// @param timeout 锁的超时时间，超过该时间锁会自动释放。单位 `秒`
  LockAdaptor of(String name, int timeout);
}
