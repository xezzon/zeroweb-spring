package io.github.xezzon.zeroweb.common.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.Map;

/**
 * @author xezzon
 */
public class DataPermissionForbiddenException extends ZerowebBusinessException {

  public static final String ERROR_CODE = "C0007";

  public DataPermissionForbiddenException(String groupId, String userId, String permission) {
    super(
        Map.ofEntries(
            Map.entry("groupId", groupId),
            Map.entry("userId", userId),
            Map.entry("permission", permission)
        ),
        String.format("`%s` has no permission `%s` for `%s`.", userId, permission, groupId)
    );
  }

  @Override
  public String getCode() {
    return ERROR_CODE;
  }

  @Override
  public int getHttpStatus() {
    return HttpResponseStatus.FORBIDDEN.code();
  }
}
