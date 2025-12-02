/*
 * SPDX-FileCopyrightText: Copyright (C) 2025 xezzon
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This file is part of ZeroWeb.
 *
 * ZeroWeb is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * ZeroWeb is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with ZeroWeb. If not, see <https://www.gnu.org/licenses/>.
 */

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
