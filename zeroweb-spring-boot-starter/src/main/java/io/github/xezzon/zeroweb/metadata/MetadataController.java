package io.github.xezzon.zeroweb.metadata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xezzon
 */
@RestController
@RequestMapping("/metadata")
public class MetadataController {

  @Value("${spring.application.name}")
  private String appName;
  @Value("${spring.application.version}")
  private String appVersion;

  /**
   * 服务自省
   * @return 服务信息
   */
  @GetMapping("/info.json")
  public ServiceInfo loadServiceInfo() {
    ServiceInfo serviceInfo = new ServiceInfo();
    serviceInfo.setName(appName);
    serviceInfo.setVersion(appVersion);
    serviceInfo.setType(ServiceType.SERVER);
    serviceInfo.setHidden(true);
    return serviceInfo;
  }
}
