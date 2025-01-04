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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据校验：仅允许字母、数字、下划线
 * @author xezzon
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
    ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {AlphanumericValidator.class})
public @interface Alphanumeric {

  /**
   * 大写字母
   */
  String ASCII_UPPER_CASE = "A-Z";
  /**
   * 小写字母
   */
  String ASCII_LOWER_CASE = "a-z";
  /**
   * 数字
   */
  String DIGIT = "0-9";
  /**
   * 下划线
   */
  String UNDERSCORE = "_";
  /**
   * 短横线
   */
  String HYPHEN = "\\-";
  /**
   * 点
   */
  String DOT = "\\.";

  /**
   * @return 包含的字符集
   */
  String[] includes() default {ASCII_UPPER_CASE, ASCII_LOWER_CASE, DIGIT, UNDERSCORE, HYPHEN, DOT};

  /**
   * @return 不包含的字符集，优先级高于 includes
   */
  String[] excludes() default {};

  /**
   * Hibernate Validator 要求的字段之一
   * @return 发生异常时的提示信息
   */
  String message() default "{io.github.xezzon.zeroweb.common.validator.Alphanumeric.message}";

  /**
   * Hibernate Validator 要求的字段之一
   */
  Class<?>[] groups() default {};

  /**
   * Hibernate Validator 要求的字段之一
   */
  Class<? extends Payload>[] payload() default {};
}

class AlphanumericValidator implements ConstraintValidator<Alphanumeric, String> {

  private String expression = ".*";

  @Override
  public void initialize(Alphanumeric annotation) {
    Set<String> includes = Arrays.stream(annotation.includes())
        .parallel()
        .collect(Collectors.toSet());
    Set<String> excludes = Arrays.stream(annotation.excludes())
        .parallel()
        .collect(Collectors.toSet());
    this.expression = includes.parallelStream()
        .filter(o -> !excludes.contains(o))
        .collect(Collectors.joining(""));
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value.matches(regex());
  }

  private String regex() {
    return Optional.ofNullable(expression)
        .map(s -> "[" + s + "]*")
        .orElse(".*");
  }
}
