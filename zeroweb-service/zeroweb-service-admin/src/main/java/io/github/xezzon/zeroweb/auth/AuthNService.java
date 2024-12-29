package io.github.xezzon.zeroweb.auth;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.auth0.jwt.JWTCreator.Builder;
import io.github.xezzon.zeroweb.auth.domain.JwtClaimWrapper;
import io.github.xezzon.zeroweb.common.exception.InvalidTokenException;
import io.github.xezzon.zeroweb.crypto.service.JwtCryptoService;
import io.github.xezzon.zeroweb.user.domain.User;
import io.github.xezzon.zeroweb.user.service.IUserService4Auth;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 * @author xezzon
 */
@Service
public class AuthNService {

  private final IUserService4Auth userService;
  private final JwtCryptoService jwtCryptoService;

  public AuthNService(IUserService4Auth userService, JwtCryptoService jwtCryptoService) {
    this.userService = userService;
    this.jwtCryptoService = jwtCryptoService;
  }

  /**
   * 校验用户名、口令
   * 校验通过后将用户信息写入 Session
   * @param username 用户名
   * @param password 口令
   * @throws InvalidTokenException 用户不存在时抛出异常
   * @throws InvalidTokenException 用户名、密码不匹配时抛出异常
   */
  protected void basicLogin(String username, String password) {
    User user = userService.getUserByUsername(username);
    /* 校验用户名、口令 */
    if (user == null) {
      throw new InvalidTokenException();
    }
    if (!BCrypt.checkpw(password, user.getCipher())) {
      throw new InvalidTokenException();
    }
    /* 检查是否已存在会话 */
    if (StpUtil.isLogin()) {
      JwtClaim claim = JwtAuth.loadJwtClaim();
      if (Objects.equals(claim.getSubject(), user.getId())) {
        // 原会话是同一个用户，则不作处理
        return;
      } else {
        // 原会话不是同一个用户，则需要将原会话作废
        StpUtil.logout();
      }
    }
    /* 写入 Session */
    JwtClaim claim = JwtClaim.newBuilder()
        .setSubject(user.getId())
        .setPreferredUsername(user.getUsername())
        .setNickname(user.getNickname())
        .build();
    StpUtil.login(claim.getSubject());
    JwtAuth.saveJwtClaim(claim);
  }

  /**
   * 生成并返回JWT（JSON Web Token）签名。 JWT中包含认证信息
   * @return 返回生成的JWT签名字符串
   */
  protected String signJwt() {
    JwtClaim claim = JwtAuth.loadJwtClaim();
    Builder jwtBuilder = new JwtClaimWrapper(claim).into();
    return jwtCryptoService.signJwt(jwtBuilder);
  }
}
