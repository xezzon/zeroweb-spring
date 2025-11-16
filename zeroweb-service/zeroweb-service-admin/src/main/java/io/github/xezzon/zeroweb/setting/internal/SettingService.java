package io.github.xezzon.zeroweb.setting.internal;

import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.setting.Setting;
import io.github.xezzon.zeroweb.setting.repository.SettingRepository;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class SettingService {

  private final SettingRepository settingRepository;

  public SettingService(final SettingRepository settingRepository) {
    this.settingRepository = settingRepository;
  }

  void addSetting(final Setting setting) {
    this.checkRepeat(setting);
    settingRepository.save(setting);
  }

  private void checkRepeat(final Setting setting) {
    Optional<Setting> exist = settingRepository.findByKey(setting.getKey());
    if (exist.isPresent() && !Objects.equals(exist.get().getId(), setting.getId())) {
      throw new RepeatDataException(setting.getKey());
    }
  }
}
