package io.github.xezzon.geom.third_party_app;

import cn.dev33.satoken.stp.StpUtil;
import io.github.xezzon.geom.common.exception.DataPermissionForbiddenException;
import io.github.xezzon.geom.core.odata.ODataQueryOption;
import io.github.xezzon.geom.openapi.domain.Openapi;
import io.github.xezzon.geom.openapi.service.IOpenapiService4ThirdPartApp;
import io.github.xezzon.geom.subscription.domain.Subscription;
import io.github.xezzon.geom.subscription.service.ISubscriptionService4ThirdPartyApp;
import io.github.xezzon.geom.third_party_app.domain.AccessSecret;
import io.github.xezzon.geom.third_party_app.domain.ThirdPartyApp;
import io.github.xezzon.geom.third_party_app.repository.AccessSecretRepository;
import io.github.xezzon.geom.third_party_app.service.IThirdPartyAppService;
import jakarta.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
@Slf4j
public class ThirdPartyAppService implements IThirdPartyAppService {

  public static final String ALGORITHM = "AES";
  private static final int AES_KEY_LENGTH = 256;
  private final ThirdPartyAppDAO thirdPartyAppDAO;
  private final ISubscriptionService4ThirdPartyApp subscriptionService;
  private final IOpenapiService4ThirdPartApp openapiService;
  private final AccessSecretRepository accessSecretRepository;

  public ThirdPartyAppService(
      ThirdPartyAppDAO thirdPartyAppDAO,
      @Lazy ISubscriptionService4ThirdPartyApp subscriptionService,
      @Lazy IOpenapiService4ThirdPartApp openapiService,
      AccessSecretRepository accessSecretRepository
  ) {
    this.thirdPartyAppDAO = thirdPartyAppDAO;
    this.subscriptionService = subscriptionService;
    this.openapiService = openapiService;
    this.accessSecretRepository = accessSecretRepository;
  }

  @Transactional()
  protected AccessSecret addThirdPartyApp(ThirdPartyApp thirdPartyApp) {
    thirdPartyAppDAO.get().save(thirdPartyApp);
    return this.rollAccessSecret(thirdPartyApp.getId());
  }

  protected Page<ThirdPartyApp> listThirdPartyAppByUser(ODataQueryOption odata, String userId) {
    return thirdPartyAppDAO.findAllWithUserId(odata, userId);
  }

  protected Page<ThirdPartyApp> listThirdPartyApp(ODataQueryOption odata) {
    return thirdPartyAppDAO.findAll(odata);
  }

  protected Page<Subscription> listSubscription(ODataQueryOption odata, String appId) {
    this.checkPermission(appId);
    Page<Openapi> openapiPage = openapiService.listPublishedOpenapi(odata);
    Set<String> openapiCodes = openapiPage.getContent().parallelStream()
        .map(Openapi::getCode)
        .collect(Collectors.toSet());
    List<Subscription> subscriptions =
        subscriptionService.listSubscriptionsOfApp(appId, openapiCodes);
    Map<String, Subscription> subscriptionMap = subscriptions.parallelStream()
        .collect(Collectors.toMap(Subscription::getOpenapiCode, s -> s));
    subscriptions = openapiPage.getContent().parallelStream()
        .map(openapi -> {
          Subscription subscription = subscriptionMap.get(openapi.getCode());
          if (subscription == null) {
            subscription = new Subscription();
          }
          subscription.setOpenapi(openapi);
          return subscription;
        })
        .toList();
    return new PageImpl<>(subscriptions, openapiPage.getPageable(), openapiPage.getTotalElements());
  }

  /**
   * 更新密钥
   * @param appId 应用标识
   * @return 更新后的应用访问凭据与密钥
   */
  protected AccessSecret rollAccessSecret(String appId) {
    this.checkPermission(appId);
    try {
      KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
      keyGenerator.init(AES_KEY_LENGTH);
      SecretKey secretKey = keyGenerator.generateKey();
      AccessSecret accessSecret = new AccessSecret();
      accessSecret.setId(appId);
      accessSecret.setSecretKey(Base64.getEncoder()
          .encodeToString(secretKey.getEncoded())
      );
      accessSecretRepository.updateSecretKeyById(accessSecret.getId(), accessSecret.getSecretKey());
      return accessSecret;
    } catch (NoSuchAlgorithmException e) {
      log.error("Cannot create key pair.", e);
    }
    return null;
  }

  @Override
  public void checkPermission(String appId) {
    Optional<ThirdPartyApp> thirdPartyApp = thirdPartyAppDAO.get().findById(appId);
    if (thirdPartyApp.isEmpty()
        || !Objects.equals(thirdPartyApp.get().getOwnerId(), StpUtil.getLoginId())
    ) {
      throw new DataPermissionForbiddenException("应用不存在或无权访问");
    }
  }
}
