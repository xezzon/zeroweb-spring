package io.github.xezzon.zeroweb.auth;

import cn.dev33.satoken.stp.StpInterface;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * @author xezzon
 */
@Component
public class JwtStpInterface implements StpInterface {

  @Override
  public List<String> getPermissionList(Object loginId, String loginType) {
    JwtClaim jwtClaim = JwtAuth.get();
    if (jwtClaim == null) {
      return Collections.emptyList();
    }
    return jwtClaim.getEntitlementsList();
  }

  @Override
  public List<String> getRoleList(Object loginId, String loginType) {
    JwtClaim jwtClaim = JwtAuth.get();
    if (jwtClaim == null) {
      return Collections.emptyList();
    }
    return jwtClaim.getRolesList();
  }
}
