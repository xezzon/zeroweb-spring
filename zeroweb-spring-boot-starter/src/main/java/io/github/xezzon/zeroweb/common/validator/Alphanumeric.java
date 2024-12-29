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

  String message() default "{io.github.xezzon.zeroweb.common.validator.Alphanumeric.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

class AlphanumericValidator implements ConstraintValidator<Alphanumeric, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value.matches("\\w*");
  }
}
