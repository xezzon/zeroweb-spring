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

import static io.github.xezzon.zeroweb.crypto.constant.ZxcvbnConstant.ZXCVBN;

import com.nulabinc.zxcvbn.Strength;
import io.github.xezzon.zeroweb.crypto.entity.PasswordStrength;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 口令、密钥相关的 HTTP 接口
 * @author xezzon
 */
@RestController
@RequestMapping
public class CryptoHttpEndpoint {

  /// 计算口令强度。
  /// @param password 口令。
  /// @param username 用户名。包含用户名的密码的强度评级将会降低。
  /// @return 口令强度。
  /// @see <a href="https://www.usenix.org/conference/usenixsecurity16/technical-sessions/presentation/wheeler">zxcvbn: Low-Budget Password Strength Estimation</a>
  @GetMapping("/password-strength")
  PasswordStrength passwordStrength(
      final String password,
      @RequestParam(required = false) final String username
  ) {
    List<String> directories = Stream.of(username)
        .filter(Objects::nonNull)
        .filter(String::isBlank)
        .toList();
    Strength measure = ZXCVBN.measure(password, directories);
    return PasswordStrength.from(measure);
  }
}
