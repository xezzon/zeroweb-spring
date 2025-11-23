package io.github.xezzon.zeroweb.crypto;

import io.github.xezzon.zeroweb.auth.JwtClaim;
import org.jspecify.annotations.NonNull;

/// @author xezzon
public interface JwtCryptoService {

  /// 签发JWT
  /// @param claim jwt构造器
  /// @return JWT字符串
  String signJwt(@NonNull JwtClaim claim);
}
