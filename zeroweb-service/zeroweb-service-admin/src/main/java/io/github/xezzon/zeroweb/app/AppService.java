package io.github.xezzon.zeroweb.app;

import io.github.xezzon.zeroweb.app.domain.App;
import io.github.xezzon.zeroweb.locale.event.I18nMessageChangedEvent;
import io.github.xezzon.zeroweb.locale.event.I18nMessageDeletedEvent;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class AppService {

  private final AppDAO appDAO;
  @Resource
  private ApplicationEventPublisher eventPublisher;

  public AppService(final AppDAO appDAO) {
    this.appDAO = appDAO;
  }

  /**
   * 新增服务
   * @param app 服务信息
   */
  void addApp(final App app) {
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
  void updateApp(final App app) {
    final App entity = appDAO.get().findById(app.getId())
        .orElseThrow(EntityNotFoundException::new);
    final App oldValue = new App();
    appDAO.getCopier().copy(entity, oldValue);
    appDAO.get().save(app);
    /* 后置处理 */
    eventPublisher.publishEvent(new I18nMessageChangedEvent(oldValue, app));
  }

  /**
   * 删除服务
   * @param id 服务ID
   */
  void deleteApp(final String id) {
    final Optional<App> app = appDAO.get().findById(id);
    if (app.isEmpty()) {
      return;
    }
    appDAO.get().deleteById(id);
    /* 后置处理 */
    eventPublisher.publishEvent(new I18nMessageDeletedEvent(app.get()));
  }
}
