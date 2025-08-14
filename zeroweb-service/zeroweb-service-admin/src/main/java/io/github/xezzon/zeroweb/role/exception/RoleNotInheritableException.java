package io.github.xezzon.zeroweb.role.exception;

import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;

/**
 * 角色不能被继承
 * @author xezzon
 */
public class RoleNotInheritableException extends ZerowebBusinessException {

  public static final String ERROR_CODE = "CFF02";

  public RoleNotInheritableException() {
    super("Add a role to a not inheritable one.");
  }

  @Override
  public String getCode() {
    return ERROR_CODE;
  }
}
