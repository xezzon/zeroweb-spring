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

package io.github.xezzon.zeroweb.common.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

/// 自定义校验注解，用于检查字符串是否仅包含字母、数字、下划线以及可选的其他字符。
/// 默认情况下，允许大写字母 (A-Z)、小写字母 (a-z)、数字 (0-9)、下划线 (_)、短横线 (-) 和点 (.)。
/// 可以通过 [#includes()] 和 [#excludes()] 属性来定制允许或禁止的字符集。
///
/// 示例用法：
/// ```java
/// public class MyDto {
///     @Alphanumeric(includes = {Alphanumeric.ASCII_UPPER_CASE, Alphanumeric.DIGIT})
///     private String username;
///
///     @Alphanumeric(excludes = {Alphanumeric.UNDERSCORE})
///     private String tagName;
/// }
/// ```
///
/// @author xezzon
@Target({
    ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
    ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {AlphanumericValidator.class})
public @interface Alphanumeric {

  /**
   * 大写字母
   * 字符集范围：A-Z。
   */
  String ASCII_UPPER_CASE = "A-Z";
  /**
   * 小写字母
   * 字符集范围：a-z。
   */
  String ASCII_LOWER_CASE = "a-z";
  /**
   * 数字
   * 字符集范围：0-9。
   */
  String DIGIT = "0-9";
  /**
   * 特殊字符：下划线 `_`。
   */
  String UNDERSCORE = "_";
  /**
   * 特殊字符：短横线 `-`。
   */
  String HYPHEN = "\\-";
  /**
   * 特殊字符：点 `.`。
   */
  String DOT = "\\.";

  /// 指定校验允许包含的字符集。
  /// 默认包含大写字母、小写字母、数字、下划线、短横线和点。
  ///
  /// @return 允许包含的字符集数组。
  String[] includes() default {ASCII_UPPER_CASE, ASCII_LOWER_CASE, DIGIT, UNDERSCORE, HYPHEN, DOT};

  /// 指定校验不允许包含的字符集。
  /// 此属性优先级高于 [#includes()]，即如果一个字符既在 includes 又在 excludes 中，则该字符将被排除。
  ///
  /// @return 不允许包含的字符集数组。
  String[] excludes() default {};

  /// 校验失败时返回的默认错误信息。
  ///
  /// @return 错误信息模板键。
  @SuppressWarnings("unused")
  String message() default "{io.github.xezzon.zeroweb.common.validator.Alphanumeric.message}";

  /// 校验组，允许将约束注解应用于不同的验证场景。
  ///
  /// @return 校验组数组。
  @SuppressWarnings("unused")
  Class<?>[] groups() default {};

  /// 负载信息，允许在校验失败时携带额外的信息。
  ///
  /// @return 负载类型数组。
  @SuppressWarnings("unused")
  Class<? extends Payload>[] payload() default {};
}

/// [Alphanumeric] 注解的验证器实现。
/// 负责根据注解中定义的字符集规则验证字符串内容。
class AlphanumericValidator implements ConstraintValidator<Alphanumeric, String> {

  /// 用于匹配允许字符的正则表达式模式。
  private Pattern pattern = Pattern.compile(".*");

  /// 初始化验证器，根据 [Alphanumeric] 注解的配置构建正则表达式。
  ///
  /// @param annotation 要应用的 [Alphanumeric] 注解实例。
  @Override
  public void initialize(final Alphanumeric annotation) {
    final Set<String> excludes = Arrays.stream(annotation.excludes())
        .collect(Collectors.toSet());
    final String expression = Arrays.stream(annotation.includes())
        .filter(o -> !excludes.contains(o))
        .collect(Collectors.joining("", "[", "]*"));
    this.pattern = Pattern.compile(expression);
  }

  /// 检查给定的字符串值是否符合 [Alphanumeric] 注解的规则。
  ///
  /// @param value   要验证的字符串值。
  /// @param context 约束验证器上下文。
  /// @return 如果字符串为 `null` 或只包含允许的字符，则返回 `true`；否则返回 `false`。
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    String invalidCharacter = pattern.matcher(value).replaceAll("");
    context.unwrap(HibernateConstraintValidatorContext.class)
        .addMessageParameter("0", invalidCharacter)
        .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addConstraintViolation()
    ;
    return invalidCharacter.isEmpty();
  }
}
