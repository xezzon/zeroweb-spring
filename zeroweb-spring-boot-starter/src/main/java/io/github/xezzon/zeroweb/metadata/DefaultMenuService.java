package io.github.xezzon.zeroweb.metadata;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class DefaultMenuService implements IMenuService {

  @Override
  public List<MenuInfo> list() {
    return Collections.emptyList();
  }
}
