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

package io.github.xezzon.zeroweb.crypto;

import com.nulabinc.zxcvbn.Strength;
import io.github.xezzon.zeroweb.crypto.exception.PasswordStrengthException;

/**
 * 口令相关服务的抽象接口
 * @author xezzon
 */
public interface IPasswordService {

  /// 校验口令强度是否符合要求。
  /// @param strength 口令强度
  /// @throws PasswordStrengthException 口令强度不达标
  void checkStrength(Strength strength);
}
