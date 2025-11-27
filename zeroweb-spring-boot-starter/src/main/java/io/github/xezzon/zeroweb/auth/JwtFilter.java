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

package io.github.xezzon.zeroweb.auth;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.BEARER;

import cn.dev33.satoken.stp.StpUtil;
import io.github.xezzon.zeroweb.common.exception.InvalidTokenException;
import io.github.xezzon.zeroweb.common.exception.ZerowebRuntimeException;
import io.github.xezzon.zeroweb.core.crypto.ASN1PublicKeyReader;
import io.github.xezzon.zeroweb.core.crypto.DerStringReader;
import io.github.xezzon.zeroweb.core.crypto.SecretKeyUtil;
import io.github.xezzon.zeroweb.core.error.BreakException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 处理请求头中 JWT 携带的认证信息。
 * @author xezzon
 */
@Slf4j
@Component
@WebFilter(urlPatterns = "/*")
@Order(8)
public class JwtFilter implements Filter {

  public static final String PUBLIC_KEY_HEADER = "X-Public-Key";
  public static final String ACCESS_KEY_HEADER = "X-Access-Key";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    JwtClaim claim = null;
    try {
      if (request instanceof HttpServletRequest httpRequest) {
        claim = this.getJwtClaim(httpRequest);
        final long timeout = claim.getExi();
        StpUtil.login(claim.getSub(), timeout);
      }
    } catch (BreakException | ZerowebRuntimeException _) {
      // 流程控制中断，无需任何处理
    } catch (Exception e) {
      // 解析JWT失败，视为没有携带Token，不影响正常的流程执行
      log.error("Failed to parse the JWT", e);
    }
    try {
      ScopedValue.where(JwtAuth.CLAIM, claim)
          .call(() -> {
            chain.doFilter(request, response);
            return null;
          });
    } catch (Exception e) {
      throw switch (e) {
        case ServletException se -> throw se;
        case IOException ie -> throw ie;
        default -> new ZerowebRuntimeException(e);
      };
    }
  }

  public JwtClaim validateWithPublicKey(final String token, final String publicKeyASN1) {
    try {
      ASN1PublicKeyReader asn1Reader = new DerStringReader(publicKeyASN1);
      ECPublicKey publicKey = (ECPublicKey) SecretKeyUtil.readPublicKey(asn1Reader);
      return JsonWebToken.decoder(publicKey).decode(token);
    } catch (Exception e) {
      log.error("Failed to parse the JWT. token: {}; key: {}", token, publicKeyASN1, e);
      throw new InvalidTokenException(e);
    }
  }

  public JwtClaim validateWithAccessKey(final String token, final String accessKey) {
    return JsonWebToken.decoder(Base64.getDecoder().decode(accessKey))
        .decode(token);
  }

  private JwtClaim getJwtClaim(HttpServletRequest request) throws BreakException {
    String authorization = request.getHeader(AUTHORIZATION);
    if (authorization == null || !authorization.startsWith(BEARER)) {
      throw new BreakException();
    }
    String token = authorization.substring(BEARER.length()).trim();
    String publicKeyASN1 = request.getHeader(PUBLIC_KEY_HEADER);
    String accessKey = request.getHeader(ACCESS_KEY_HEADER);
    if (publicKeyASN1 != null && !publicKeyASN1.isEmpty()) {
      // 前端调用经过网关验证，使用公钥验证
      return validateWithPublicKey(token, publicKeyASN1);
    } else if (accessKey != null && !accessKey.isEmpty()) {
      // 第三方系统调用经过网关验证，使用AccessKey验证
      return validateWithAccessKey(token, accessKey);
    } else {
      throw new BreakException();
    }
  }
}
