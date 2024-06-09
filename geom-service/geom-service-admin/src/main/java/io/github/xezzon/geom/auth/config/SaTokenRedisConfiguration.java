package io.github.xezzon.geom.auth.config;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;

/**
 * @author xezzon
 */
@Configuration
@ConditionalOnExpression("'${REDIS_URL:}'.empty")
public class SaTokenRedisConfiguration {

  @PostConstruct
  public void init() {
    SaManager.setSaTokenDao(new SaTokenDaoDefaultImpl());
  }
}
