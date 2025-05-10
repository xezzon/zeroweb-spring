package io.github.xezzon.zeroweb.common.metadata;

import io.github.xezzon.zeroweb.metadata.IMenuService;
import io.github.xezzon.zeroweb.metadata.MenuInfo;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@SuppressWarnings("unused")
@Service
public class MenuService implements IMenuService {

  @Override
  public List<MenuInfo> list() {
    return Stream.of(
            PermissionConstant.PERMISSIONS
        )
        .flatMap(Collection::stream)
        .toList();
  }
}
