package io.github.xezzon.geom.auth.config;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import io.github.xezzon.geom.common.redis.RedisTemplateFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xezzon
 */
@Configuration
@ConditionalOnMissingBean(RedisTemplateFactory.class)
public class SaTokenRedisConfiguration {

  @PostConstruct
  public void init() {
    SaManager.setSaTokenDao(new SaTokenDaoDefaultImpl());
  }
}
