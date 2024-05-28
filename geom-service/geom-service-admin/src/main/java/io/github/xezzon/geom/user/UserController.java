package io.github.xezzon.geom.user;

import cn.hutool.crypto.digest.BCrypt;
import io.github.xezzon.geom.common.domain.Id;
import io.github.xezzon.geom.user.domain.RegisterUserReq;
import io.github.xezzon.geom.user.domain.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xezzon
 */
@RequestMapping("/user")
@RestController
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public Id register(@RequestBody @Validated RegisterUserReq req) {
    User user = req.into();
    String cipher = BCrypt.hashpw(req.getPassword(), BCrypt.gensalt());
    user.setCipher(cipher);
    userService.addUser(user);
    return Id.of(user.getId());
  }
}
