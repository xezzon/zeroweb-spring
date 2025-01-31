package io.github.xezzon.zeroweb.common.validator;

import lombok.Getter;
import lombok.Setter;

/**
 * @author xezzon
 */
@Getter
@Setter
public class ChildEntity {

  @Alphanumeric(
      includes = {
          Alphanumeric.ASCII_LOWER_CASE, Alphanumeric.ASCII_UPPER_CASE, Alphanumeric.DOT,
      },
      excludes = {
          Alphanumeric.DOT
      }
  )
  private String alphabet;
  @Alphanumeric()
  private String empty;
}
