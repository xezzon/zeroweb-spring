package io.github.xezzon.geom.third_party_app;

import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class ThirdPartyAppService {

  private final ThirdPartyAppDAO thirdPartyAppDAO;

  public ThirdPartyAppService(ThirdPartyAppDAO thirdPartyAppDAO) {
    this.thirdPartyAppDAO = thirdPartyAppDAO;
  }
}
