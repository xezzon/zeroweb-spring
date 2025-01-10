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
  @SuppressWarnings("unused")
  String message() default "{io.github.xezzon.zeroweb.common.validator.Alphanumeric.message}";

  /**
   * Hibernate Validator 要求的字段之一
   */
  @SuppressWarnings("unused")
  Class<?>[] groups() default {};

  /**
   * Hibernate Validator 要求的字段之一
   */
  @SuppressWarnings("unused")
  Class<? extends Payload>[] payload() default {};
}

class AlphanumericValidator implements ConstraintValidator<Alphanumeric, String> {

  private Pattern pattern = Pattern.compile(".*");

  @Override
  public void initialize(final Alphanumeric annotation) {
    final Set<String> excludes = Arrays.stream(annotation.excludes())
        .collect(Collectors.toSet());
    final String expression = Arrays.stream(annotation.includes())
        .filter(o -> !excludes.contains(o))
        .collect(Collectors.joining("", "[", "]*"));
    this.pattern = Pattern.compile(expression);
  }

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
