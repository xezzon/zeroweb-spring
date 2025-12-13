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
import io.jsonwebtoken.security.JwkSet;
import io.jsonwebtoken.security.Jwks;
import jakarta.validation.constraints.NotBlank;
import java.security.interfaces.ECPublicKey;
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

  private final JwtKeyManager jwtKeyManager;

  /// 依赖注入
  /// @param jwtKeyManager JWT 密钥管理
  public CryptoHttpEndpoint(final JwtKeyManager jwtKeyManager) {
    this.jwtKeyManager = jwtKeyManager;
  }

  /// 计算口令强度。
  /// @param password 口令。
  /// @param username 用户名。包含用户名的密码的强度评级将会降低。
  /// @return 口令强度。
  /// @see <a href="https://www.usenix.org/conference/usenixsecurity16/technical-sessions/presentation/wheeler">zxcvbn: Low-Budget Password Strength Estimation</a>
  @GetMapping("/password-strength")
  PasswordStrength passwordStrength(
      @RequestParam @NotBlank final String password,
      @RequestParam(required = false) final String username
  ) {
    List<String> directories = Stream.of(username)
        .filter(Objects::nonNull)
        .filter(s -> !s.isBlank())
        .toList();
    Strength measure = ZXCVBN.measure(password, directories);
    return PasswordStrength.from(measure);
  }

  /**
   * 以 JWKs 的形式对外暴露用于签名的公钥
   * @return JWK 集合。如果有多条数据，从前往后，优先级递减。
   * @see <a href="https://tools.ietf.org/html/rfc7517">JSON Web Key</a>
   */
  @GetMapping("/well-known/jwks.json")
  JwkSet wellKnownJwks() {
    ECPublicKey publicKey = jwtKeyManager.getPublicKey();
    return Jwks.set()
        .add(Jwks.builder()
            .key(publicKey)
            .build()
        )
        .build();
  }
}
