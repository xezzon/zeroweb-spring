package io.github.xezzon.zeroweb.metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class MenuService implements IMenuService {

  public static final List<MenuInfo> MENU_INFOS = new ArrayList<>();

  static {
    final MenuInfo route = new MenuInfo();
    route.setType(MenuType.ROUTE);
    route.setPath("/article/[articleId]");
    route.setPermissions(List.of("article:write", "article:read"));
    MENU_INFOS.add(route);
    final MenuInfo externalLink = new MenuInfo();
    externalLink.setType(MenuType.EXTERNAL_LINK);
    externalLink.setPath("https://cloudflare.com");
    externalLink.setPermissions(Collections.emptyList());
    MENU_INFOS.add(externalLink);
    final MenuInfo embedded = new MenuInfo();
    embedded.setType(MenuType.EMBEDDED);
    embedded.setPath("https://github.com");
    embedded.setPermissions(Collections.singletonList("github"));
    MENU_INFOS.add(embedded);
    final MenuInfo permission = new MenuInfo();
    permission.setType(MenuType.PERMISSION);
    permission.setPath("article:write");
    permission.setPermissions(Collections.singletonList("article:write"));
    MENU_INFOS.add(permission);
    final MenuInfo groupPermission = new MenuInfo();
    groupPermission.setType(MenuType.GROUP_PERMISSION);
    groupPermission.setPath("subscription:#:create");
    groupPermission.setPermissions(Collections.singletonList("subscription:#:create"));
    MENU_INFOS.add(groupPermission);
  }

  @Override
  public List<MenuInfo> list() {
    return MENU_INFOS;
  }
}
