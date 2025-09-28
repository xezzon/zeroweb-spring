package io.github.xezzon.zeroweb.crypto;

import io.github.xezzon.zeroweb.auth.JwtClaim;
import org.jetbrains.annotations.NotNull;

/// @author xezzon
public interface JwtCryptoService {

  /// 签发JWT
  /// @param claim jwt构造器
  /// @return JWT字符串
  String signJwt(@NotNull JwtClaim claim);
}
