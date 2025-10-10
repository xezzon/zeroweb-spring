package io.github.xezzon.zeroweb.common.otlp;

import cn.dev33.satoken.stp.StpUtil;
import io.github.xezzon.zeroweb.auth.JwtAuth;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @see <a href="https://opentelemetry.io/docs/specs/semconv/attributes-registry/user/">User | OpenTelemetry</a>
 */
@Slf4j
@Component
@WebFilter(urlPatterns = "/*")
@Order(16)
public class UserSpanAttributeFilter implements Filter {

  public static final String USER_ID = "user.id";
  public static final String USER_NAME = "user.name";
  public static final String USER_ROLES = "user.roles";
  public static final String USER_FULL_NAME = "user.full_name";


  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    try {
      if (StpUtil.isLogin()) {
        Span span = Span.current();
        JwtAuth.get().ifPresent(claimWrapper -> {
          span.setAttribute(USER_ID, claimWrapper.getSub());
          span.setAttribute(USER_NAME, claimWrapper.getPreferredUsername());
          span.setAttribute(AttributeKey.stringArrayKey(USER_ROLES), claimWrapper.getRolesList());
          span.setAttribute(USER_FULL_NAME, claimWrapper.getNickname());
        });
      }
    } catch (RuntimeException _) {
      // ignored
    }
    chain.doFilter(request, response);
  }
}
