package io.github.xezzon.zeroweb.auth;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static com.google.auth.http.AuthHttpConstants.BEARER;
import static io.github.xezzon.zeroweb.auth.JwtClaimWrapper.DEFAULT_TIMEOUT;

import cn.dev33.satoken.stp.StpUtil;
import io.github.xezzon.zeroweb.common.exception.InvalidTokenException;
import io.github.xezzon.zeroweb.common.exception.ZerowebRuntimeException;
import io.github.xezzon.zeroweb.core.crypto.ASN1PublicKeyReader;
import io.github.xezzon.zeroweb.core.crypto.DerStringReader;
import io.github.xezzon.zeroweb.core.crypto.SecretKeyUtil;
import io.github.xezzon.zeroweb.core.error.BreakException;
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
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author xezzon
 */
@Slf4j
@Component
@WebFilter(urlPatterns = "/*")
@Order(8)
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
          throw new BreakException();
        }
        String token = authorization.substring(BEARER.length()).trim();
        String publicKeyASN1 = httpRequest.getHeader(PUBLIC_KEY_HEADER);
        String accessKey = httpRequest.getHeader(ACCESS_KEY_HEADER);
        JwtClaimWrapper claimWrapper;
        if (publicKeyASN1 != null && !publicKeyASN1.isEmpty()) {
          // 前端调用经过网关验证，使用公钥验证
          claimWrapper = validateWithPublicKey(token, publicKeyASN1);
        } else if (accessKey != null && !accessKey.isEmpty()) {
          // 第三方系统调用经过网关验证，使用AccessKey验证
          claimWrapper = validateWithAccessKey(token, accessKey);
        } else {
          throw new BreakException();
        }
        Long timeout = Optional.ofNullable(claimWrapper.getExi()).orElse(DEFAULT_TIMEOUT);
        StpUtil.login(claimWrapper.getSub(), timeout);
        JwtAuth.save(claimWrapper);
      }
    } catch (BreakException | ZerowebRuntimeException ignored) {
      // 流程控制中断，无需任何处理
    } catch (Exception e) {
      // 解析JWT失败，视为没有携带Token，不影响正常的流程执行
      log.error("Failed to parse the JWT", e);
    }
    chain.doFilter(request, response);
    JwtAuth.clear();
  }

  public JwtClaimWrapper validateWithPublicKey(String token, String publicKeyASN1) {
    try {
      ASN1PublicKeyReader asn1Reader = new DerStringReader(publicKeyASN1);
      ECPublicKey publicKey = (ECPublicKey) SecretKeyUtil.readPublicKey(asn1Reader);
      return JsonWebToken.decoder(publicKey).decode(token);
    } catch (Exception e) {
      log.error("Failed to parse the JWT. token: {}; key: {}", token, publicKeyASN1, e);
      throw new InvalidTokenException(e);
    }
  }

  public JwtClaimWrapper validateWithAccessKey(String token, String accessKey) {
    return JsonWebToken.decoder(Base64.getDecoder().decode(accessKey))
        .decode(token);
  }
}
