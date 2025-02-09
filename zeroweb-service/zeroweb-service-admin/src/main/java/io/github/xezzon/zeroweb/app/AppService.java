package io.github.xezzon.zeroweb.app;

import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class AppService {

  private final AppDAO appDAO;

  public AppService(final AppDAO appDAO) {
    this.appDAO = appDAO;
  }
}
