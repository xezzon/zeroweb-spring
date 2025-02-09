package io.github.xezzon.zeroweb.app;

import io.github.xezzon.zeroweb.app.domain.App;
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

  /**
   * 新增服务
   * @param app 服务信息
   */
  void addApp(App app) {
    appDAO.get().save(app);
  }
}
