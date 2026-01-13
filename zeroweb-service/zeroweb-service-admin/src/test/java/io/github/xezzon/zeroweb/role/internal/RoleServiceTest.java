package io.github.xezzon.zeroweb.role.internal;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.role.Role;
import io.github.xezzon.zeroweb.role.RoleConstant;
import io.github.xezzon.zeroweb.role.entity.AddRoleReq;
import io.github.xezzon.zeroweb.role.exception.RoleNotInheritableException;
import io.github.xezzon.zeroweb.role.repository.RoleRepository;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
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
      role.setParentId(RoleConstant.ADMIN_ID);
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
          role.getChildren().add(child);
        }
      }
      dataset.add(role);
    }
  }

  @Test
  void addRole_success() {
    Role req1 = new AddRoleReq(
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        true,
        RoleConstant.ADMIN_ID
    ).into();
    roleService.addRole(req1);
    Role actual1 = repository.findById(req1.getId()).orElseThrow();
    Assertions.assertEquals(req1.getCode(), actual1.getValue());
    Assertions.assertEquals(req1.getName(), actual1.getName());

    Role req2 = new AddRoleReq(
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        false,
        req1.getId()
    ).into();
    roleService.addRole(req2);
    Role actual2 = repository.findById(req2.getId()).orElseThrow();
    Assertions.assertEquals(req1.getValue() + "/" + req2.getCode(), actual2.getValue());
  }

  @Test
  void addRole_parentNotExist() {
    Role req = new AddRoleReq(
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        RandomUtil.randomBoolean(),
        RandomUtil.randomString(8)
    ).into();
    Assertions.assertThrows(
        RoleNotInheritableException.class,
        () -> roleService.addRole(req)
    );
  }

  @Test
  void addRole_parentNotInheritable() {
    Role req = new AddRoleReq(
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        RandomUtil.randomBoolean(),
        RoleConstant.SUPER_ID
    ).into();
    Assertions.assertThrows(
        RoleNotInheritableException.class,
        () -> roleService.addRole(req)
    );
  }

  @Test
  void addRole_repeat() {
    Role role = dataset.getFirst();
    Role req = new AddRoleReq(
        role.getCode(),
        RandomUtil.randomString(8),
        RandomUtil.randomBoolean(),
        role.getParentId()
    ).into();
    Assertions.assertThrows(
        RepeatDataException.class,
        () -> roleService.addRole(req)
    );
  }

  @Test
  @Transactional
  void deleteRole_success() {
    Role role = dataset.getFirst();

    roleService.deleteRole(role.getId());
    Assertions.assertFalse(repository.existsById(role.getId()));

    roleService.deleteRole(RoleConstant.ADMIN_ID);
    Role child = dataset.stream()
        .filter(Role::getInheritable)
        .findAny().orElseThrow();
    Assertions.assertFalse(repository.existsById(child.getId()));
    Assertions.assertFalse(repository.existsById(child.getChildren().getFirst().getId()));
  }
}
