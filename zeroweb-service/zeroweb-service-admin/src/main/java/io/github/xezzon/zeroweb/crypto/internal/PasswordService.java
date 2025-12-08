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

package io.github.xezzon.zeroweb.crypto.internal;

import com.nulabinc.zxcvbn.Strength;
import io.github.xezzon.zeroweb.crypto.IPasswordService;
import io.github.xezzon.zeroweb.crypto.entity.PasswordStrength;
import io.github.xezzon.zeroweb.crypto.exception.PasswordStrengthException;
import io.github.xezzon.zeroweb.setting.ISettingService;
import io.github.xezzon.zeroweb.setting.Setting;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

/**
 * 口令相关服务
 * @author xezzon
 */
@Service
public class PasswordService implements IPasswordService {

  private final ISettingService settingService;

  PasswordService(final ISettingService settingService) {
    this.settingService = settingService;
  }

  /**
   * 检查计算出的强度与配置的要求是否一致
   */
  @Override
  public void checkStrength(final Strength strength) {
    Setting setting;
    try {
      setting = settingService.queryByCode("password.strength");
    } catch (NoSuchElementException _) {
      // 没有设置强度要求
      return;
    }
    PasswordStrength requirement = setting.convertValueTo(new TypeReference<>() {
    });
    if (requirement.getScore() != null && strength.getScore() < requirement.getScore()) {
      throw new PasswordStrengthException(requirement.getScore(), strength.getScore());
    }
  }
}
