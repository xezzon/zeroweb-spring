package io.github.xezzon.zeroweb.crypto.service;

import com.auth0.jwt.JWTCreator;
import org.jetbrains.annotations.NotNull;

/**
 * @author xezzon
 */
public interface JwtCryptoService {

  /**
   * 签发JWT
   * @param jwtBuilder jwt构造器
   * @return JWT字符串
   */
  String signJwt(@NotNull JWTCreator.Builder jwtBuilder);
}
