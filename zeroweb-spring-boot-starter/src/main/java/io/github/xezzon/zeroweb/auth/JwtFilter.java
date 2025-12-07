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

/// 处理请求头中 JWT 携带的认证信息，支持通过公钥或 AccessKey 进行验证。
/// JWT 有效期将作为 Sa-Token 的登录超时时间。
///
/// 优先级：
/// 1. 请求头中 `X-Public-Key` 存在时，优先使用公钥验证。
/// 2. 请求头中 `X-Access-Key` 存在时，优先使用 AccessKey 验证。
/// 3. 如果以上两者都不存在，则不进行 JWT 验证，流程继续。
///
/// 注意：
/// - 若 JWT 解析失败或无效，将视为未携带 Token，不影响后续流程。
/// - JWT 的 `sub` 字段将作为 Sa-Token 的登录 ID。
///
/// @author xezzon
@Slf4j
@Component
@WebFilter(urlPatterns = "/*")
@Order(8)
public class JwtFilter implements Filter {

  /// 公钥的请求头名称。前端认证时该请求头不能为空。
  public static final String PUBLIC_KEY_HEADER = "X-Public-Key";
  /// Access Key 的请求头名称。第三方应用访问时该请求头不能为空。
  public static final String ACCESS_KEY_HEADER = "X-Access-Key";

  /// 执行 JWT 认证过滤。
  /// 从 HTTP 请求头中获取 JWT 信息，并进行验证，然后将用户信息绑定到 Sa-Token 会话。
  ///
  /// @param request ServletRequest 对象
  /// @param response ServletResponse 对象
  /// @param chain FilterChain 对象
  /// @throws ServletException 如果发生 Servlet 相关错误
  /// @throws IOException 如果发生 I/O 错误
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

  /// 使用公钥验证 JWT。
  ///
  /// @param token JWT 字符串
  /// @param publicKeyASN1 ASN.1 格式的公钥字符串
  /// @return 解析后的 JwtClaim 对象
  /// @throws InvalidTokenException 如果 token 无效或公钥解析失败
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

  /// 使用 AccessKey 验证 JWT。
  ///
  /// @param token JWT 字符串
  /// @param accessKey Base64 编码的 AccessKey 字符串
  /// @return 解析后的 JwtClaim 对象
  /// @throws InvalidTokenException 如果 token 无效或 AccessKey 解析失败
  public JwtClaim validateWithAccessKey(final String token, final String accessKey) {
    return JsonWebToken.decoder(Base64.getDecoder().decode(accessKey))
        .decode(token);
  }

  /// 从 HTTP 请求中提取 JWT 的 Claim 信息。
  /// 优先使用 `X-Public-Key` 进行公钥验证，其次使用 `X-Access-Key` 进行 AccessKey 验证。
  /// 如果请求头中未携带有效的 `Authorization`、`X-Public-Key` 或 `X-Access-Key`，则抛出 `BreakException`。
  ///
  /// @param request HttpServletRequest 对象
  /// @return 解析后的 JwtClaim 对象
  /// @throws BreakException 如果无法从请求中获取有效的 JWT 信息
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
