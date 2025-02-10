package io.github.xezzon.zeroweb.app;

import io.github.xezzon.zeroweb.app.domain.App;
import java.util.List;
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

  /**
   * 查询服务列表
   * @return 服务列表
   */
  List<App> listApp() {
    return appDAO.get().findAllByOrderByOrdinalAsc();
  }

  /**
   * 更新服务信息
   * @param app 服务信息
   */
  void updateApp(App app) {
    appDAO.partialUpdate(app);
  }
}
