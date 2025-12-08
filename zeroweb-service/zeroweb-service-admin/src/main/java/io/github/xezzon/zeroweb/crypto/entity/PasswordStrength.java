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

package io.github.xezzon.zeroweb.crypto.entity;

import com.nulabinc.zxcvbn.Strength;
import io.github.xezzon.zeroweb.core.trait.From;
import lombok.Getter;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author xezzon
 */
@Getter
@Setter
public class PasswordStrength {

  /// 密码强度设置的业务参数键值
  public static final String SETTING_KEY = "password.strength";

  /// 口令强度分数
  private Integer score;
  /// 口令强度数量级
  private Double guessesLog10;

  /// 将 zxcvbn [Strength] 转换为 [PasswordStrength]。
  /// @param strength zxcvbn 的 Strength 对象
  /// @return 口令强度字段
  public static PasswordStrength from(Strength strength) {
    return Converter.INSTANCE.from(strength);
  }

  /// 将 zxcvbn [Strength] 转换为 [PasswordStrength]。
  /// 仅保留 [Strength#score] 和 [Strength#guessesLog10]，遮蔽其他字段。
  @Mapper
  public interface Converter extends From<Strength, PasswordStrength> {

    /// [Converter] 的实例
    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Override
    PasswordStrength from(Strength source);
  }
}
