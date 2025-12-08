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

package io.github.xezzon.zeroweb.crypto.exception;

import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;
import org.jspecify.annotations.NullMarked;

/// 口令强度不符合要求
/// @author xezzon
@NullMarked
public class PasswordStrengthException extends ZerowebBusinessException {

  /// 错误码
  public static final String ERROR_CODE = "CFF03";

  /// 口令强度得分不达标
  /// @param score 实际得分
  /// @param requirement 要求得分
  public PasswordStrengthException(int score, int requirement) {
    super(String.format(
        "The strength of this password does not meet the requirements. The required strength is %s, but the current strength is %s.",
        requirement, score
    ));
  }

  @Override
  public String code() {
    return ERROR_CODE;
  }
}
