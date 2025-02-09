package io.github.xezzon.zeroweb.app;

import io.github.xezzon.zeroweb.app.domain.AddAppReq;
import io.github.xezzon.zeroweb.app.domain.App;
import io.github.xezzon.zeroweb.common.domain.Id;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务管理
 * @author xezzon
 */
@RestController
@RequestMapping("/app")
public class AppController {

  private final AppService appService;

  public AppController(final AppService appService) {
    this.appService = appService;
  }

  /**
   * 新增服务
   * @param req 服务基础信息
   */
  @PostMapping()
  public Id addApp(@RequestBody @Validated AddAppReq req) {
    App app = req.into();
    appService.addApp(app);
    return Id.of(app.getId());
  }

  /**
   * 查询服务列表
   * @return 服务列表
   */
  @GetMapping()
  public List<App> listApp() {
    return appService.listApp();
  }
}
