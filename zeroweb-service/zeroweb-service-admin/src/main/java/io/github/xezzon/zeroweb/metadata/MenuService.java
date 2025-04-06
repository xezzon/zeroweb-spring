package io.github.xezzon.zeroweb.metadata;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
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
