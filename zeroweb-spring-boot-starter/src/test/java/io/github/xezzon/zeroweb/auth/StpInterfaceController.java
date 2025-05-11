package io.github.xezzon.zeroweb.auth;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xezzon
 */
@RestController
@RequestMapping("/check")
public class StpInterfaceController {

  @GetMapping("/role")
  @SaCheckRole({"test"})
  public List<String> checkRole() {
    return StpUtil.getRoleList();
  }

  @GetMapping("/permission")
  @SaCheckPermission({"any"})
  public List<String> checkPermission() {
    return StpUtil.getPermissionList();
  }
}
