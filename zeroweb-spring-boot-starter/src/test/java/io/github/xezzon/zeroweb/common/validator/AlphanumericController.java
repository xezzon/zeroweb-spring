package io.github.xezzon.zeroweb.common.validator;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xezzon
 */
@RestController()
public class AlphanumericController {

  @PostMapping("/alphanumeric/validate")
  public void validate(@RequestBody @Valid ValidEntity entity) {
    throw new UnsupportedOperationException();
  }

  @GetMapping("/alphanumeric/validate")
  public void validate(@RequestParam @Alphanumeric String alphabet) {
    throw new UnsupportedOperationException();
  }
}
