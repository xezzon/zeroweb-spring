package io.github.xezzon.zeroweb.user;

import cn.hutool.crypto.digest.BCrypt;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.user.domain.RegisterUserReq;
import io.github.xezzon.zeroweb.user.domain.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户功能HTTP接口
 * @author xezzon
 */
@RequestMapping("/user")
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * 用户注册
   * @param req 用户名、昵称、密码等
   * @return 用户ID
   */
  @PostMapping("/register")
  public Id register(@RequestBody @Validated RegisterUserReq req) {
    User user = req.into();
    String cipher = BCrypt.hashpw(req.getPassword(), BCrypt.gensalt());
    user.setCipher(cipher);
    userService.addUser(user);
    return Id.of(user.getId());
  }
}
