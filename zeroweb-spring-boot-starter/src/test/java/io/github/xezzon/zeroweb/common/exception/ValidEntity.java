package io.github.xezzon.zeroweb.common.exception;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xezzon
 */
@Getter
@Setter
public class ValidEntity {

  @Size(min = 16)
  private String name;

  @Email
  private String email;
}
