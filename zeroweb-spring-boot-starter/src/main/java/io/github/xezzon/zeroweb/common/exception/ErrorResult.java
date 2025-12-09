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

package io.github.xezzon.zeroweb.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

/// `ErrorResult` 是一个通用的 API 异常响应对象，用于封装错误信息。
/// 它与 `Error-Code` 响应头对应，提供了一致的错误表示方式。
/// 此类旨在通过标准化的结构返回错误码、错误消息和可选的详细信息。
///
/// @author xezzon
/// @version 1.0
/// @see ZerowebBusinessException
@SuppressWarnings("NotNullFieldNotInitialized")
@Getter
public class ErrorResult {

  /// 服务端定义的一组错误码。
  /// 通常用于标识错误的类型，可用于国际化或前端逻辑判断。
  private String code;
  /// 错误的可读表述。
  /// 应该是一个对用户友好的消息，用于解释错误发生的原因。
  private String message;
  /// 用于消息插值的参数。
  /// 当错误消息包含占位符时，此映射提供具体的值。
  private Map<String, Object> parameters = Collections.emptyMap();
  /// 有关导致该报告错误的具体错误的详细信息数组。
  /// 当一个错误由多个子错误引起时，可以使用此字段提供更详细的上下文。
  @JsonInclude(Include.NON_NULL)
  private @Nullable List<Detail> details;

  /// 使用 Throwable 对象构造一个 ErrorResult。
  /// 它会从异常中提取错误码和消息，并检查是否为 [ZerowebBusinessException]
  /// 以获取额外的参数。
  ///
  /// @param e 原始的 [Throwable] 异常
  public ErrorResult(final Throwable e) {
    this.code = getCode(e);
    this.message = e.getLocalizedMessage();
    if (e instanceof ZerowebBusinessException zbe) {
      this.parameters = zbe.getParameters();
    }
  }

  /// 使用 Throwable 对象和详细信息列表构造一个 ErrorResult。
  ///
  /// @param e 原始的 [Throwable] 异常
  /// @param details 导致该报告错误的具体错误的详细信息列表
  public ErrorResult(final Throwable e, final List<Detail> details) {
    this(e);
    this.details = details;
  }

  /// 构造一个带有指定错误码、消息和参数的 ErrorResult。
  /// 主要用于内部构造，例如 [Detail] 类。
  ///
  /// @param code 服务端定义的一组错误码
  /// @param message 错误的可读表述
  /// @param parameters 用于消息插值的参数
  protected ErrorResult(String code, String message, Map<String, Object> parameters) {
    this.code = code;
    this.message = message;
    this.parameters = parameters;
  }

  /// 默认构造函数，供 Jackson 等 JSON 序列化库使用。
  @SuppressWarnings("unused")
  ErrorResult() {
  }

  /// 从给定的 [Throwable] 对象中提取错误码。
  /// 如果异常类的简单名称以 "Exception" 结尾，则移除该后缀作为错误码；否则使用完整的简单名称。
  ///
  /// @param e 原始的 [Throwable] 异常
  /// @return 提取出的错误码字符串
  public static String getCode(final Throwable e) {
    String name = e.getClass().getSimpleName();
    final String suffix = "Exception";
    return name.endsWith(suffix) ? name.substring(0, name.length() - suffix.length()) : name;
  }

  /// `Detail` 是 [ErrorResult] 的一个嵌套类，用于表示错误的详细信息。
  /// 它继承了 `ErrorResult` 的结构，允许以嵌套的方式提供更具体的错误描述。
  public static class Detail extends ErrorResult {

    /// 默认构造函数，供 Jackson 等 JSON 序列化库使用。
    @SuppressWarnings("unused")
    public Detail() {
      super();
    }

    /// 构造一个带有指定错误码、消息和参数的详细错误信息。
    ///
    /// @param code 服务端定义的一组错误码
    /// @param message 错误的可读表述
    /// @param parameters 用于消息插值的参数
    public Detail(String code, String message, Map<String, Object> parameters) {
      super(code, message, parameters);
    }
  }
}
