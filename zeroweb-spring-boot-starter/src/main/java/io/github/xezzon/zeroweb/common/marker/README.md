# 标记(marker)接口

标记接口是一类特殊的接口，它不包含任何方法。标记接口的主要作用是说明实现类的实现方法，需要与其他的接口组合使用。

以实现加锁功能为例。

首先，定义加锁接口。

```java
interface Lock {

  void lock(String key);

  void unlock(String key);
}
```

接下来，定义与实现不同的加锁实现方式以及它们的优先级。

```java
import org.springframework.core.annotation.Order;

/**
 * 基于内存实现的功能
 */
@Order(8)
interface InMemoryTrait {

}

/**
 * 基于Redis实现的功能
 */
@Order(4)
interface RedisTrait {

}

/**
 * 基于ZooKeeper实现的功能
 */
@Order(2)
interface ZooKeeperTrait {

}
```

```java
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 基于内存实现加锁功能
 */
@Component  // 在没有配置Redis的情况下，InMemoryTrait的优先级最高，注入Lock的实例是InMemoryLock。
class InMemoryLock implements Lock, InMemoryTrait {

  @Override
  public void lock(String key) {
    // ...
  }

  @Override
  public void unlock(String key) {
    // ...
  }
}

/**
 * 基于Redis实现加锁功能
 */
@ConditionalOnBean(RedisTemplate.class)  // 在配置了 Redis 的情况下，RedisTrait的优先级更高，注入Lock的实例是RedisLock。
@Component
class RedisLock implements Lock, RedisTrait {

  @Override
  public void lock(String key) {
    // ...
  }

  @Override
  public void unlock(String key) {
    // ...
  }
}
```

在此基础上即可灵活地为一种功能定义不同的实现，并根据部署的配置不同使用不同的方式实现功能。
