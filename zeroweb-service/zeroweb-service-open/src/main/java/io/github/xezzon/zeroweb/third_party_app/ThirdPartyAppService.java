package io.github.xezzon.zeroweb.third_party_app;

import cn.dev33.satoken.stp.StpUtil;
import com.auth0.jwt.JWTCreator.Builder;
import io.github.xezzon.zeroweb.ZerowebOpenConstant;
import io.github.xezzon.zeroweb.auth.JwtAuth;
import io.github.xezzon.zeroweb.auth.JwtClaim;
import io.github.xezzon.zeroweb.auth.domain.JwtClaimWrapper;
import io.github.xezzon.zeroweb.common.config.ZerowebConfig;
import io.github.xezzon.zeroweb.common.config.ZerowebConfig.ZerowebJwtConfig;
import io.github.xezzon.zeroweb.common.exception.DataPermissionForbiddenException;
import io.github.xezzon.zeroweb.common.exception.InvalidAccessKeyException;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.third_party_app.domain.AccessSecret;
import io.github.xezzon.zeroweb.third_party_app.domain.ThirdPartyApp;
import io.github.xezzon.zeroweb.third_party_app.repository.AccessSecretRepository;
import io.github.xezzon.zeroweb.third_party_app.service.IThirdPartyAppService;
import io.github.xezzon.zeroweb.third_party_app.service.IThirdPartyAppService4Call;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
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
public class ThirdPartyAppService implements IThirdPartyAppService, IThirdPartyAppService4Call {

  public static final String ALGORITHM = "AES";
  private static final int AES_KEY_LENGTH = 256;
  private final ThirdPartyAppDAO thirdPartyAppDAO;
  private final AccessSecretRepository accessSecretRepository;
  private final ZerowebJwtConfig zerowebJwtConfig;

  public ThirdPartyAppService(
      ThirdPartyAppDAO thirdPartyAppDAO,
      AccessSecretRepository accessSecretRepository,
      ZerowebConfig zerowebConfig
  ) {
    this.thirdPartyAppDAO = thirdPartyAppDAO;
    this.accessSecretRepository = accessSecretRepository;
    this.zerowebJwtConfig = zerowebConfig.getJwt();
  }

  /**
   * 添加第三方应用并生成访问密钥
   * @param thirdPartyApp 要添加的第三方应用对象
   * @return 生成的访问密钥对象
   */
  @Transactional()
  protected AccessSecret addThirdPartyApp(ThirdPartyApp thirdPartyApp) {
    thirdPartyAppDAO.get().save(thirdPartyApp);
    return this.rollAccessSecret(thirdPartyApp.getId());
  }

  /**
   * 根据用户ID分页查询第三方应用列表
   * @param odata OData查询选项，用于指定分页和排序等条件
   * @param userId 用户ID
   * @return 分页查询结果，包含符合条件的第三方应用列表
   */
  protected Page<ThirdPartyApp> listThirdPartyAppByUser(ODataQueryOption odata, String userId) {
    return thirdPartyAppDAO.findAllWithUserId(odata, userId);
  }

  /**
   * 分页查询第三方应用列表
   * @param odata OData查询选项，用于指定分页和排序等条件
   * @return 分页查询结果，包含符合条件的第三方应用列表
   */
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
  public String signJwt(String accessKey, byte[] body, String signature, Instant iat)
      throws InvalidAccessKeyException {
    /* 校验摘要 */
    String appId = new String(
        Base64.getDecoder().decode(accessKey),
        StandardCharsets.UTF_8
    );
    this.validateSignature(appId, body, signature);
    /* 构造JWT */
    ThirdPartyApp thirdPartyApp = thirdPartyAppDAO.get().getReferenceById(appId);
    JwtClaim claim = JwtClaim.newBuilder()
        .setSubject(appId)
        .setPreferredUsername(thirdPartyApp.getId())
        .setNickname(thirdPartyApp.getName())
        .addAllEntitlements(Collections.singleton("*"))
        .build();
    Builder jwtBuilder = new JwtClaimWrapper(claim).into();
    Instant exp = iat.plusSeconds(zerowebJwtConfig.getTimeout());
    jwtBuilder
        .withIssuer(zerowebJwtConfig.getIssuer())
        .withIssuedAt(iat)
        .withExpiresAt(exp)
        .withJWTId(UUID.randomUUID().toString());
    return new JwtAuth(
        Base64.getDecoder().decode(accessKey)
    ).sign(jwtBuilder);
  }

  /**
   * 校验摘要
   * @param appId 应用标识
   * @param body 消息体
   * @param signature 摘要
   * @throws InvalidAccessKeyException 签名校验失败
   */
  private void validateSignature(String appId, byte[] body, String signature) {
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
}
