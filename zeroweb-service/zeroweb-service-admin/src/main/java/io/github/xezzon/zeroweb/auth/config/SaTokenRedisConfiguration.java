package io.github.xezzon.zeroweb.auth.config;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import io.github.xezzon.zeroweb.common.redis.RedisTemplateFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;

/// 应用启动时检查是否配置了 Redis
///
/// 没有配置 Redis 时，使用内存作为 Session 的存储方式
///
/// 否则以 Redis 作为 Session 的存储方式
/// @author xezzon
@Configuration
@ConditionalOnMissingBean(RedisTemplateFactory.class)
public class SaTokenRedisConfiguration implements ApplicationRunner {

  @Override
  public void run(final ApplicationArguments args) {
    SaManager.setSaTokenDao(new SaTokenDaoDefaultImpl());
  }
}
