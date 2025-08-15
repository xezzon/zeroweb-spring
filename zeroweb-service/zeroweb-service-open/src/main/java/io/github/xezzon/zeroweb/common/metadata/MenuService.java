package io.github.xezzon.zeroweb.common.metadata;

import io.github.xezzon.zeroweb.metadata.IMenuService;
import io.github.xezzon.zeroweb.metadata.MenuInfo;
import io.github.xezzon.zeroweb.subscription.authz.SubscriptionPermissionConstant;
import io.github.xezzon.zeroweb.third_party_app.authz.ThirdPartyAppPermissionConstant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@SuppressWarnings("unused")
@Service
public class MenuService implements IMenuService {

  @Override
  public List<MenuInfo> list() {
    return Stream.of(
            PermissionConstant.getPermissions(),
            ThirdPartyAppPermissionConstant.getPermissions(),
            SubscriptionPermissionConstant.getPermissions()
        )
        .flatMap(Collection::stream)
        .toList();
  }
}
