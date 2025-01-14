package io.github.xezzon.zeroweb.locale;

import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.locale.domain.Language;
import io.github.xezzon.zeroweb.locale.entity.AddLanguageReq;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 语言管理
 *
 * @author xezzon
 */
@RestController
@RequestMapping("/language")
public class LanguageController {

  private final LocalizedService localizedService;

  LanguageController(LocalizedService localizedService) {
    this.localizedService = localizedService;
  }

  /**
   * 新增语言
   * @param req 语言
   */
  @PostMapping()
  public Id addLanguage(@RequestBody AddLanguageReq req) {
    Language language = req.into();
    localizedService.addLanguage(language);
    return Id.of(language.getId());
  }
}
