package io.github.xezzon.zeroweb.locale;

import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.locale.domain.Translation;
import io.github.xezzon.zeroweb.locale.entity.UpsertTranslationReq;
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
@RequestMapping("/locale")
public class TranslationController {

  private final LocalizedService localizedService;

  public TranslationController(final LocalizedService localizedService) {
    this.localizedService = localizedService;
  }

  /**
   * 新增/更新 国际化文本
   * @param req 国际化文本
   */
  @PutMapping()
  public Id upsertTranslation(@RequestBody final UpsertTranslationReq req) {
    final Translation translation = req.into();
    localizedService.upsertTranslation(translation);
    return Id.of(translation.getId());
  }

  /**
   * 加载国际化资源
   * @param language 国际化语言
   * @param namespace 命名空间
   * @return 国际化内容-国际化文本
   */
  @GetMapping("/{language}/{namespace}")
  public Map<String, String> loadTranslation(
      @PathVariable final String language,
      @PathVariable final String namespace
  ) {
    return localizedService.loadTranslation(language, namespace);
  }
}
