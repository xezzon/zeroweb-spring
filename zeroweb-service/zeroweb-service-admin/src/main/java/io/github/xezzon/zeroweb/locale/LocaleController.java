package io.github.xezzon.zeroweb.locale;

import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.locale.domain.I18nMessage;
import io.github.xezzon.zeroweb.locale.entity.AddI18nMessageReq;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 国际化内容及文本管理
 *
 * @author xezzon
 */
@RestController
@RequestMapping("/locale")
public class LocaleController {

  private final LocalizedService localizedService;

  public LocaleController(LocalizedService localizedService) {
    this.localizedService = localizedService;
  }

  @PostMapping()
  public Id addI18nMessage(@RequestBody AddI18nMessageReq req) {
    I18nMessage i18nMessage = req.into();
    localizedService.addI18nMessage(i18nMessage);
    return Id.of(i18nMessage.getId());
  }
}
