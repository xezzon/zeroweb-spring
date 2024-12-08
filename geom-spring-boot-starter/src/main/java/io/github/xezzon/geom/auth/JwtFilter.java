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
import java.util.Base64;
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
  public static final String ACCESS_KEY_HEADER = "X-Access-Key";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    try {
      if (request instanceof HttpServletRequest httpRequest) {
        String authorization = httpRequest.getHeader(AUTHORIZATION);
        if (authorization == null || !authorization.startsWith(BEARER)) {
          return;
        }
        String token = authorization.substring(BEARER.length()).trim();
        String publicKeyASN1 = httpRequest.getHeader(PUBLIC_KEY_HEADER);
        String accessKey = httpRequest.getHeader(ACCESS_KEY_HEADER);
        JwtClaim claim;
        if (publicKeyASN1 != null && !publicKeyASN1.isEmpty()) {
          // 前端调用经过网关验证，使用公钥验证
          claim = validateWithPublicKey(token, publicKeyASN1);
        } else if (accessKey != null && !accessKey.isEmpty()) {
          // 第三方系统调用经过网关验证，使用AccessKey验证
          claim = validateWithAccessKey(token, accessKey);
        } else {
          return;
        }
        StpUtil.login(claim.getSubject());
        JwtAuth.saveJwtClaim(claim);
      }
    } finally {
      chain.doFilter(request, response);
    }
  }

  public JwtClaim validateWithPublicKey(String token, String publicKeyASN1) {
    try {
      ASN1PublicKeyReader asn1Reader = new DerStringReader(publicKeyASN1);
      ECPublicKey publicKey = (ECPublicKey) SecretKeyUtil.readPublicKey(asn1Reader);
      return new JwtAuth(publicKey).decode(token);
    } catch (Exception e) {
      log.error("Failed to parse the JWT. token: {}; key: {}", token, publicKeyASN1, e);
      throw new GeomRuntimeException(ErrorCode.INVALID_TOKEN, e);
    }
  }

  public JwtClaim validateWithAccessKey(String token, String accessKey) {
    return new JwtAuth(
        Base64.getDecoder().decode(accessKey)
    ).decode(token);
  }
}
