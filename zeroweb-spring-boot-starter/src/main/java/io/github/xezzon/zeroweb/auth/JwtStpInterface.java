package io.github.xezzon.zeroweb.auth;

import cn.dev33.satoken.stp.StpInterface;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * @author xezzon
 */
@Component
@SuppressWarnings("unused")
public class JwtStpInterface implements StpInterface {

  @Override
  public List<String> getPermissionList(Object loginId, String loginType) {
    return JwtAuth.get()
        .map(JwtClaim::getEntitlementsList)
        .map(Collections::unmodifiableList)
        .orElse(Collections.emptyList());
  }

  @Override
  public List<String> getRoleList(Object loginId, String loginType) {
    return JwtAuth.get()
        .map(JwtClaim::getRolesList)
        .map(Collections::unmodifiableList)
        .orElse(Collections.emptyList());
  }
}
