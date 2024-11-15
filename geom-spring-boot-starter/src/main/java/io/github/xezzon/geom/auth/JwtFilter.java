package io.github.xezzon.geom.auth;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static com.google.auth.http.AuthHttpConstants.BEARER;

import cn.dev33.satoken.stp.StpUtil;
import io.github.xezzon.geom.common.exception.ErrorCode;
import io.github.xezzon.geom.common.exception.GeomRuntimeException;
import io.github.xezzon.geom.core.crypto.ASN1PublicKeyReader;
import io.github.xezzon.geom.core.crypto.DerStringReader;
import io.github.xezzon.geom.core.crypto.SecretKeyUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.interfaces.ECPublicKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author xezzon
 */
@Slf4j
@Component
@WebFilter(urlPatterns = "/*")
public class JwtFilter implements Filter {

  public static final String PUBLIC_KEY_HEADER = "X-Public-Key";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    if (request instanceof HttpServletRequest httpRequest) {
      String authorization = httpRequest.getHeader(AUTHORIZATION);
      String publicKeyASN1 = httpRequest.getHeader(PUBLIC_KEY_HEADER);
      if (authorization == null || publicKeyASN1 == null) {
        chain.doFilter(request, response);
        return;
      }
      if (!authorization.startsWith(BEARER) || publicKeyASN1.isEmpty()) {
        chain.doFilter(request, response);
        return;
      }
      String token = authorization.substring(BEARER.length()).trim();
      try {
        ASN1PublicKeyReader asn1Reader = new DerStringReader(publicKeyASN1);
        ECPublicKey publicKey = (ECPublicKey) SecretKeyUtil.readPublicKey(asn1Reader);
        JwtClaim claim = new JwtAuth(publicKey).decode(token);
        StpUtil.login(claim.getSubject());
        JwtAuth.saveJwtClaim(claim);
      } catch (Exception e) {
        log.error("Failed to parse the JWT. token: {}; key: {}", token, publicKeyASN1, e);
        throw new GeomRuntimeException(ErrorCode.INVALID_TOKEN, e);
      }
      chain.doFilter(request, response);
    }
  }
}
