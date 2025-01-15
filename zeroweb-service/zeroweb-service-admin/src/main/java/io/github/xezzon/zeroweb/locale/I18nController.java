package io.github.xezzon.zeroweb.locale;

import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.locale.domain.I18nText;
import io.github.xezzon.zeroweb.locale.entity.UpsertI18nTextReq;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 国际化文本管理
 */
@RestController
@RequestMapping("/i18n")
public class I18nController {

  private final LocalizedService localizedService;

  public I18nController(LocalizedService localizedService) {
    this.localizedService = localizedService;
  }

  /**
   * 新增/更新 国际化文本
   * @param req 国际化文本
   */
  @PutMapping()
  public Id upsertI18nText(@RequestBody UpsertI18nTextReq req) {
    I18nText i18nText = req.into();
    localizedService.upsertI18nText(i18nText);
    return Id.of(i18nText.getId());
  }

  /**
   * 加载国际化资源
   * @param language 国际化语言
   * @param namespace 命名空间
   * @return 国际化内容-国际化文本
   */
  @GetMapping("/{language}/{namespace}")
  public Map<String, String> loadI18nText(
      @PathVariable String language,
      @PathVariable String namespace
  ) {
    return localizedService.loadI18nText(language, namespace);
  }
}
