package io.github.xezzon.zeroweb.third_party_app;

import cn.dev33.satoken.stp.StpUtil;
import io.github.xezzon.zeroweb.ZerowebOpenConstant;
import io.github.xezzon.zeroweb.common.exception.DataPermissionForbiddenException;
import io.github.xezzon.zeroweb.common.exception.InvalidAccessKeyException;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.third_party_app.domain.AccessSecret;
import io.github.xezzon.zeroweb.third_party_app.domain.ThirdPartyApp;
import io.github.xezzon.zeroweb.third_party_app.repository.AccessSecretRepository;
import io.github.xezzon.zeroweb.third_party_app.service.IThirdPartyAppService;
import jakarta.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
  private final AccessSecretRepository accessSecretRepository;

  public ThirdPartyAppService(
      ThirdPartyAppDAO thirdPartyAppDAO,
      AccessSecretRepository accessSecretRepository
  ) {
    this.thirdPartyAppDAO = thirdPartyAppDAO;
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

  @Override
  public void validateSignature(String appId, byte[] body, String signature) {
    AccessSecret accessSecret = accessSecretRepository.findById(appId)
        .orElseThrow(InvalidAccessKeyException::new);
    try {
      Mac mac = Mac.getInstance(ZerowebOpenConstant.DIGEST_ALGORITHM);
      byte[] secretKey = Base64.getDecoder().decode(accessSecret.getSecretKey());
      mac.init(new SecretKeySpec(secretKey, ZerowebOpenConstant.DIGEST_ALGORITHM));
      mac.update(body);
      if (!Objects.equals(
          signature,
          Base64.getEncoder().encodeToString(mac.doFinal())
      )) {
        throw new InvalidAccessKeyException();
      }
    } catch (InvalidAccessKeyException e) {
      throw e;
    } catch (Exception e) {
      throw new InvalidAccessKeyException();
    }
  }

  @Override
  public ThirdPartyApp findById(String appId) {
    return thirdPartyAppDAO.get()
        .getReferenceById(appId);
  }
}
