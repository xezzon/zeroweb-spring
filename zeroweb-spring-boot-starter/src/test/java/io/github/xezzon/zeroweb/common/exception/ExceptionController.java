package io.github.xezzon.zeroweb.common.exception;

import jakarta.persistence.EntityNotFoundException;
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
    throw new RepeatDataException("数据");
  }

  @RequestMapping("/NoValidClasspathException")
  public void noValidClasspathException() {
    throw new NoValidClasspathException(new RuntimeException());
  }

  @RequestMapping("/EntityNotFoundException")
  public void entityNotFoundException() {
    throw new EntityNotFoundException();
  }

  @RequestMapping("/UnsupportedOperationException")
  public void unsupportedOperationException() {
    throw new UnsupportedOperationException("unsupported operation");
  }

  @RequestMapping("/MethodArgumentNotValidException")
  public void methodArgumentNotValidException(@RequestBody @Validated ValidEntity entity) {
    throw new UnsupportedOperationException(entity.getName());
  }

  @RequestMapping("/DataPermissionForbiddenException")
  public void dataPermissionForbiddenException() {
    throw new DataPermissionForbiddenException("无权访问该应用");
  }
}
