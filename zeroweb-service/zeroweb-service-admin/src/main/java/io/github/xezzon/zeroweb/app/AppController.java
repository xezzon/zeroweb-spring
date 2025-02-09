package io.github.xezzon.zeroweb.app;

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
}
