package io.github.xezzon.geom.crypto;

import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import io.github.xezzon.geom.common.config.GeomConfig;
import io.github.xezzon.geom.common.config.GeomConfig.GeomJwtConfig;
import io.github.xezzon.geom.crypto.service.JwtCryptoService;
import java.time.Instant;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @author xezzon
 */
@Component
public class CryptoService implements
    JwtCryptoService {

  private final GeomJwtConfig geomJwtConfig;
  private final KeyManager keyManager;

  public CryptoService(GeomConfig geomConfig, KeyManager keyManager) {
    this.geomJwtConfig = geomConfig.getJwt();
    this.keyManager = keyManager;
  }

  @Override
  public String signJwt(@NotNull Builder jwtBuilder) {
    Instant iat = Instant.now();
    Instant exp = iat.plusSeconds(geomJwtConfig.getTimeout());
    return jwtBuilder
        .withIssuer(geomJwtConfig.getIssuer())
        .withIssuedAt(iat)
        .withExpiresAt(exp)
        .withJWTId(UUID.randomUUID().toString())
        .sign(Algorithm.ECDSA256(keyManager.getPrivateKey()));
  }
}
