package io.github.xezzon.geom.common.exception;

import java.util.NoSuchElementException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xezzon
 */
@RestController
public class ExceptionController {

  @RequestMapping("/RepeatDataException")
  public void repeatDataException() {
    throw new RepeatDataException("数据已存在");
  }

  @RequestMapping("/NoValidClasspathException")
  public void noValidClasspathException() {
    throw new NoValidClasspathException(new RuntimeException());
  }

  @RequestMapping("/NoSuchElementException")
  public void noSuchElementException() {
    throw new NoSuchElementException();
  }

  @RequestMapping("/UnsupportedOperationException")
  public void unsupportedOperationException() {
    throw new UnsupportedOperationException("unsupported operation");
  }

  @RequestMapping("/MethodArgumentNotValidException")
  public void methodArgumentNotValidException(@RequestBody @Validated ValidEntity entity) {
    throw new UnsupportedOperationException(entity.getName());
  }
}
