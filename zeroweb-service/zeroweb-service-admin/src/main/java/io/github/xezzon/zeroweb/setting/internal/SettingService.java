package io.github.xezzon.zeroweb.setting.internal;

import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.setting.Setting;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class SettingService {

  private final SettingDAO settingDAO;

  public SettingService(final SettingDAO settingDAO) {
    this.settingDAO = settingDAO;
  }

  void addSetting(final Setting setting) {
    this.checkRepeat(setting);
    settingDAO.get().save(setting);
  }

  Page<@NonNull Setting> querySettingPage(final ODataQueryOption odata) {
    return settingDAO.findAll(odata);
  }

  Setting queryByCode(@NonNull final String code) {
    return settingDAO.get().findByCode(code)
        .orElseThrow(() ->
            new NoSuchElementException("Setting `" + code + "` does not exist.")
        );
  }

  void updateSetting(final Setting setting) {
    settingDAO.partialUpdate(setting);
  }

  void deleteSetting(final String id) {
    settingDAO.get().deleteById(id);
  }

  private void checkRepeat(final Setting setting) {
    Optional<Setting> exist = settingDAO.get().findByCode(setting.getCode());
    if (exist.isPresent() && !Objects.equals(exist.get().getId(), setting.getId())) {
      throw new RepeatDataException(setting.getCode());
    }
  }
}
