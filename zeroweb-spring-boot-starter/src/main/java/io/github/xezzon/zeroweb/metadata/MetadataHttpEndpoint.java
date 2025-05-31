package io.github.xezzon.zeroweb.metadata;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务自省
 * @author xezzon
 */
@RestController
@RequestMapping("/metadata")
public class MetadataHttpEndpoint {

  @Value("${spring.application.name}")
  private String appName;
  @Value("${spring.application.version}")
  private String appVersion;
  @Resource
  private List<IMenuService> resourceServices;

  /**
   * 服务自省服务信息
   * @return 服务信息
   */
  @GetMapping("/info.json")
  public ServiceInfo loadServiceInfo() {
    final ServiceInfo serviceInfo = new ServiceInfo();
    serviceInfo.setName(appName);
    serviceInfo.setVersion(appVersion);
    serviceInfo.setType(ServiceType.SERVER);
    serviceInfo.setHidden(true);
    return serviceInfo;
  }

  /**
   * 服务自省资源信息
   * @return 资源信息
   */
  @GetMapping("/menu.json")
  public List<MenuInfo> loadResourceInfo() {
    return resourceServices.stream()
        .map(IMenuService::list)
        .flatMap(Collection::stream)
        .toList();
  }
}
