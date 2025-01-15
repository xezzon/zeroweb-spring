package io.github.xezzon.zeroweb.locale;

import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.locale.domain.I18nText;
import io.github.xezzon.zeroweb.locale.entity.UpsertI18nTextReq;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/i18n")
public class I18nController {

  private final LocalizedService localizedService;

  public I18nController(LocalizedService localizedService) {
    this.localizedService = localizedService;
  }

  @PutMapping()
  public Id upsertI18nText(@RequestBody UpsertI18nTextReq req) {
    I18nText i18nText = req.into();
    localizedService.upsertI18nText(i18nText);
    return Id.of(i18nText.getId());
  }
}
