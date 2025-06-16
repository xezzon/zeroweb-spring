package io.github.xezzon.zeroweb.role.exception;

import io.github.xezzon.zeroweb.common.exception.AdminErrorCode;
import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;
import java.util.Collections;

/**
 * 角色不能被继承
 * @author xezzon
 */
public class RoleNotInheritableException extends ZerowebBusinessException {

  public RoleNotInheritableException() {
    super(
        AdminErrorCode.ROLE_NOT_INHERITABLE,
        Collections.emptyMap(),
        "Add a role to a not inheritable one."
    );
  }
}
