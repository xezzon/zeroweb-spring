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

package io.github.xezzon.zeroweb.crypto.constant;

import com.nulabinc.zxcvbn.StandardDictionaries;
import com.nulabinc.zxcvbn.StandardKeyboards;
import com.nulabinc.zxcvbn.Zxcvbn;
import com.nulabinc.zxcvbn.ZxcvbnBuilder;
import io.github.xezzon.zeroweb.common.exception.ZerowebRuntimeException;
import java.io.IOException;

/**
 * zxcvbn 相关的常量
 * @author xezzon
 */
public class ZxcvbnConstant {

  /// 全局默认的 [Zxcvbn] 实例。加载了所有的内置字典。
  public static final Zxcvbn ZXCVBN;

  static {
    try {
      ZXCVBN = new ZxcvbnBuilder()
          .dictionaries(StandardDictionaries.loadAllDictionaries())
          .keyboards(StandardKeyboards.loadAllKeyboards())
          .build();
    } catch (IOException e) {
      throw new ZerowebRuntimeException(e);
    }
  }

  /// 私有化构造函数。防止被实例化。
  private ZxcvbnConstant() {
  }
}
