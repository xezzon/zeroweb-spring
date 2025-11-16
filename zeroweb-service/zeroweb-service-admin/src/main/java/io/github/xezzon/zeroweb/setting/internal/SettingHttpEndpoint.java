package io.github.xezzon.zeroweb.setting.internal;

import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.core.odata.ODataRequestParam;
import io.github.xezzon.zeroweb.setting.Setting;
import io.github.xezzon.zeroweb.setting.entity.AddSettingRequest;
import io.github.xezzon.zeroweb.setting.entity.UpdateSchemaRequest;
import io.github.xezzon.zeroweb.setting.entity.UpdateValueRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// 业务参数管理
/// @author xezzon
@RestController
@RequestMapping("/setting")
public class SettingHttpEndpoint {

  private final SettingService settingService;

  public SettingHttpEndpoint(final SettingService settingService) {
    this.settingService = settingService;
  }

  /// 新增业务参数
  /// @param request 业务参数
  /// @return ID
  @PostMapping()
  Id addSetting(final AddSettingRequest request) {
    return null;
  }

  /// 查询业务参数列表（分页）
  /// @param odata 查询参数
  /// @return 业务参数列表
  @GetMapping()
  Page<@NonNull Setting> querySettingPage(final ODataRequestParam odata) {
    return null;
  }

  /**
   * 更新业务参数
   * @param request 业务参数
   */
  @PutMapping("/schema")
  void updateSettingSchema(final UpdateSchemaRequest request) {
  }

  /**
   * 更新业务参数（仅更新值）
   * @param request 业务参数
   */
  @PutMapping("/value")
  void updateSettingValue(final UpdateValueRequest request) {
  }

  /**
   * 删除业务参数
   * @param id 业务参数ID
   */
  @DeleteMapping("/{id}")
  void deleteSetting(@PathVariable final String id) {
  }
}
