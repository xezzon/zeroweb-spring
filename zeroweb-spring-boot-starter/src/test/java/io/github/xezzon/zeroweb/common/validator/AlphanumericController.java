package io.github.xezzon.zeroweb.common.validator;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xezzon
 */
@RestController()
public class AlphanumericController {

  @PostMapping("/alphanumeric/validate")
  public void validate(@RequestBody @Validated ValidEntity entity) {
    throw new UnsupportedOperationException();
  }
}
