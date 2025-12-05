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

package io.github.xezzon.zeroweb.common.otlp;

import cn.dev33.satoken.stp.StpUtil;
import io.github.xezzon.zeroweb.auth.JwtAuth;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/// OTLP 用户 Span 属性过滤器。
/// 该过滤器将登录用户的相关信息（如用户 ID、用户名、角色、全名）添加到当前的 OpenTelemetry Span 中，
/// 以便在分布式追踪中识别和关联用户操作。
///
/// @see <a href="https://opentelemetry.io/docs/specs/semconv/attributes-registry/user/">User | OpenTelemetry</a>
@Slf4j
@Component
@WebFilter(urlPatterns = "/*")
@Order(16)
public class UserSpanAttributeFilter implements Filter {

  /// OpenTelemetry Span 属性键：用户 ID
  public static final String USER_ID = "user.id";
  /// OpenTelemetry Span 属性键：用户名
  public static final String USER_NAME = "user.name";
  /// OpenTelemetry Span 属性键：用户角色
  public static final String USER_ROLES = "user.roles";
  /// OpenTelemetry Span 属性键：用户全名
  public static final String USER_FULL_NAME = "user.full_name";


  /// 过滤请求，将登录用户的属性添加到当前的 Span 中。
  /// 如果用户已登录，则从 [JwtAuth] 中获取用户声明，并将其转换为 OpenTelemetry Span 属性。
  /// 任何在处理过程中发生的运行时异常都将被忽略，以确保请求链的正常执行。
  ///
  /// @param request  ServletRequest 对象
  /// @param response ServletResponse 对象
  /// @param chain    FilterChain 对象
  /// @throws IOException      如果发生 I/O 错误
  /// @throws ServletException 如果发生 Servlet 错误
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    try {
      if (StpUtil.isLogin()) {
        Span span = Span.current();
        JwtAuth.get().ifPresent(claimWrapper -> {
          span.setAttribute(USER_ID, claimWrapper.getSub());
          span.setAttribute(USER_NAME, claimWrapper.getPreferredUsername());
          span.setAttribute(AttributeKey.stringArrayKey(USER_ROLES), claimWrapper.getRolesList());
          span.setAttribute(USER_FULL_NAME, claimWrapper.getNickname());
        });
      }
    } catch (RuntimeException _) {
      // ignored
    }
    chain.doFilter(request, response);
  }
}
