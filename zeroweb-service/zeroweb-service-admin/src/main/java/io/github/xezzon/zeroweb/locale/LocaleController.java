package io.github.xezzon.zeroweb.locale;

import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.core.odata.ODataRequestParam;
import io.github.xezzon.zeroweb.locale.domain.I18nMessage;
import io.github.xezzon.zeroweb.locale.entity.AddI18nMessageReq;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

  /**
   * 新增国际化内容
   * @param req 国际化内容
   */
  @PostMapping()
  public Id addI18nMessage(@RequestBody AddI18nMessageReq req) {
    I18nMessage i18nMessage = req.into();
    localizedService.addI18nMessage(i18nMessage);
    return Id.of(i18nMessage.getId());
  }

  @GetMapping()
  public List<String> listI18nNamespace() {
    return localizedService.listI18nNamespace();
  }

  /**
   * 分页查询国际化内容
   * @param namespace 命名空间
   * @param odata 分页查询参数
   * @return 国际化内容列表
   */
  @GetMapping("/{namespace}")
  public Page<I18nMessage> queryI18nMessageList(
      @PathVariable String namespace,
      ODataRequestParam odata
  ) {
    return localizedService.queryI18nMessageList(namespace, odata.into());
  }

  /**
   * 更新国际化内容
   * @param i18nMessage 国际化内容
   */
  @PutMapping()
  public void updateI18nMessage(@RequestBody I18nMessage i18nMessage) {
    localizedService.updateI18nMessage(i18nMessage);
  }

  /**
   * 删除国际化内容
   * @param id 国际化内容ID
   */
  @DeleteMapping("/{id}")
  public void deleteI18nMessage(@PathVariable String id) {
    localizedService.deleteI18nMessage(id);
  }
}
