package io.github.xezzon.zeroweb.common.validator;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xezzon
 */
@Getter
@Setter
public class ValidEntity {

  @Alphanumeric(
      includes = {
          Alphanumeric.ASCII_LOWER_CASE, Alphanumeric.ASCII_UPPER_CASE, Alphanumeric.DOT,
      },
      excludes = {
          Alphanumeric.DOT
      }
  )
  private String alphabet;

  @Valid
  private ChildEntity childEntity;
}
