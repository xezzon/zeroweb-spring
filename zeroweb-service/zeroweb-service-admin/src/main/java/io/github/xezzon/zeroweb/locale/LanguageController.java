package io.github.xezzon.zeroweb.locale;

import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.locale.domain.Language;
import io.github.xezzon.zeroweb.locale.entity.AddLanguageReq;
import io.github.xezzon.zeroweb.locale.entity.ModifyLanguageReq;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 语言管理
 * @author xezzon
 */
@RestController
@RequestMapping("/language")
public class LanguageController {

  private final LocalizedService localizedService;

  LanguageController(final LocalizedService localizedService) {
    this.localizedService = localizedService;
  }

  /**
   * 新增语言
   * @param req 语言
   */
  @PostMapping()
  public Id addLanguage(@RequestBody final AddLanguageReq req) {
    final Language language = req.into();
    localizedService.addLanguage(language);
    return Id.of(language.getId());
  }

  /**
   * 查询语言列表
   * @return 语言列表
   */
  @GetMapping()
  public List<Language> queryLanguageList() {
    return localizedService.queryLanguageList();
  }

  /**
   * 更新语言
   * @param req 语言
   */
  @PutMapping()
  public void updateLanguage(@RequestBody final ModifyLanguageReq req) {
    final Language language = req.into();
    localizedService.updateLanguage(language);
  }

  /**
   * 删除语言
   * @param id 语言ID
   */
  @DeleteMapping("/{id}")
  public void deleteLanguage(@PathVariable final String id) {
    localizedService.deleteLanguage(id);
  }
}
