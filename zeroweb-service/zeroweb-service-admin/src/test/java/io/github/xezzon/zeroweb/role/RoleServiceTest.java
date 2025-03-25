package io.github.xezzon.zeroweb.role;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.role.domain.Role;
import io.github.xezzon.zeroweb.role.repository.RoleRepository;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@DirtiesContext
class RoleServiceTest {

  private final List<Role> dataset = new ArrayList<>();
  @Resource
  private RoleRepository repository;
  @Resource
  private RoleService roleService;

  @BeforeEach
  void setUp() {
    for (int i = 0; i < 16; i++) {
      Role role = new Role();
      role.setCode(RandomUtil.randomString(8));
      role.setValue(role.getCode());
      role.setName(RandomUtil.randomString(8));
      role.setInheritable(RandomUtil.randomBoolean());
      role.setParentId("1");
      repository.save(role);
      if (role.getInheritable()) {
        role.setChildren(new ArrayList<>());
        for (int j = 0; j < 8; j++) {
          Role child = new Role();
          child.setCode(RandomUtil.randomString(8));
          child.setValue(role.getValue() + "/" + child.getCode());
          child.setName(RandomUtil.randomString(8));
          child.setInheritable(RandomUtil.randomBoolean());
          child.setParentId(role.getId());
          repository.save(child);
          role.getChildren().add(role);
        }
      }
      dataset.add(role);
    }
  }
}
