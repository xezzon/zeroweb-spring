package io.github.xezzon.geom.third_party_app;

import io.github.xezzon.geom.third_party_app.domain.ThirdPartyApp;
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

  protected void addThirdPartyApp(ThirdPartyApp thirdPartyApp) {
    thirdPartyAppDAO.get().save(thirdPartyApp);
  }
}
