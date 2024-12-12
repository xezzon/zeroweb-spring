package io.github.xezzon.zeroweb.core.crypto;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

/**
 * 读写密钥工具类
 * @author xezzon
 */
@Slf4j
public class SecretKeyUtil {

  public static final JcaPEMKeyConverter keyConverter = new JcaPEMKeyConverter();

  private SecretKeyUtil() {
  }

  /**
   * 从ASN1PublicKeyReader中读取公钥
   * @param reader ASN1PublicKeyReader对象，用于读取公钥
   * @return 返回读取到的公钥
   * @throws PEMException 如果读取公钥失败，抛出PEMException异常
   */
  public static PublicKey readPublicKey(ASN1PublicKeyReader reader) throws PEMException {
    Object asn1;
    try {
      asn1 = reader.readPublicKey();
    } catch (Exception e) {
      throw new PEMException("Failed to read public key.", e);
    }
    SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(asn1);
    return keyConverter.getPublicKey(publicKeyInfo);
  }

  /**
   * 从ASN1PrivateKeyReader中读取私钥
   * @param reader ASN1PrivateKeyReader对象，用于读取私钥
   * @return 返回读取到的私钥
   * @throws PEMException 如果读取私钥失败，抛出PEMException异常
   */
  public static PrivateKey readPrivateKey(ASN1PrivateKeyReader reader) throws PEMException {
    Object asn1;
    try {
      asn1 = reader.readPrivateKey();
    } catch (Exception e) {
      throw new PEMException("Failed to read private key.", e);
    }
    PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(asn1);
    return keyConverter.getPrivateKey(privateKeyInfo);
  }

  /**
   * 将公钥写入ASN1PublicKeyWriter中。
   * @param publicKey 要写入的公钥对象。
   * @param writer 用于写入公钥的ASN1PublicKeyWriter对象。
   * @throws PEMException 如果写入公钥失败，抛出PEMException异常。
   */
  public static void writePublicKey(PublicKey publicKey, ASN1PublicKeyWriter writer)
      throws PEMException {
    try {
      writer.writePublicKey(publicKey.getEncoded());
    } catch (IOException e) {
      throw new PEMException("Failed to write public key.", e);
    }
  }

  /**
   * 将私钥写入ASN1PrivateKeyWriter中。
   * @param privateKey 要写入的私钥对象。
   * @param writer 用于写入私钥的ASN1PrivateKeyWriter对象。
   * @throws PEMException 如果写入私钥失败，抛出PEMException异常。
   */
  public static void writePrivateKey(PrivateKey privateKey, ASN1PrivateKeyWriter writer)
      throws PEMException {
    try {
      writer.writePrivateKey(privateKey.getEncoded());
    } catch (IOException e) {
      throw new PEMException("Failed to write public key.", e);
    }
  }
}
