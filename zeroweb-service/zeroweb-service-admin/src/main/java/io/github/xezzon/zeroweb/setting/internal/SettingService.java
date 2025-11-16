package io.github.xezzon.zeroweb.setting.internal;

import io.github.xezzon.zeroweb.setting.repository.SettingRepository;
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
}
