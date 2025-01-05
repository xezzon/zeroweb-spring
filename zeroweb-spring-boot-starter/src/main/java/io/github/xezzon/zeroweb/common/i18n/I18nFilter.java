package io.github.xezzon.zeroweb.common.i18n;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import java.io.IOException;
import org.springframework.stereotype.Component;

/**
 * @author xezzon
 */
@Component
@WebFilter(urlPatterns = "/*")
public class I18nFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    I18nUtil.setCurrentLocale(request.getLocale());
    chain.doFilter(request, response);
    I18nUtil.setCurrentLocale(null);
  }
}
