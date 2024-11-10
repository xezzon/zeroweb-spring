package io.github.xezzon.geom.third_party_app;

import io.github.xezzon.geom.core.odata.ODataQueryOption;
import io.github.xezzon.geom.third_party_app.domain.ThirdPartyApp;
import org.springframework.data.domain.Page;
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

  protected Page<ThirdPartyApp> listThirdPartyAppByUser(ODataQueryOption odata, String userId) {
    return thirdPartyAppDAO.findAllWithUserId(odata, userId);
  }
}
