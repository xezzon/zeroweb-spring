package io.github.xezzon.zeroweb.third_party_app.repository;

import io.github.xezzon.zeroweb.third_party_app.domain.ThirdPartyApp_;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

/**
 * @author xezzon
 */
public class ThirdPartyAppSpec {

  private ThirdPartyAppSpec() {
  }

  public static Sort defaultSort() {
    return Sort.by(Order.desc(ThirdPartyApp_.CREATE_TIME));
  }
}
