package io.github.xezzon.geom.core.crypto;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.geom.core.util.ResourceUtil;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.openssl.PEMException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author xezzon
 */
class SecretKeyUtilTest {

  private static PrivateKey privateKey;
  private static PublicKey publicKey;

  @BeforeAll
  static void beforeAll() throws NoSuchAlgorithmException {
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
    KeyPair keyPair = keyPairGenerator.generateKeyPair();
    privateKey = keyPair.getPrivate();
    publicKey = keyPair.getPublic();
  }

  @Test
  void pemClasspathReaderAndWriter_publicKey() throws PEMException {
    String name = RandomUtil.randomString(8);
    ASN1PublicKeyReader asn1Reader = new PemClasspathReaderAndWriter(name);
    Assertions.assertThrows(PEMException.class, () -> SecretKeyUtil.readPublicKey(asn1Reader));
    ASN1PublicKeyWriter asn1Writer = new PemClasspathReaderAndWriter(name);
    SecretKeyUtil.writePublicKey(publicKey, asn1Writer);
    Assertions.assertTrue(ResourceUtil.getResourceFromClasspath(name + ".public.pem")
        .toFile().exists()
    );
    PublicKey publicKey1 = SecretKeyUtil.readPublicKey(asn1Reader);
    Assertions.assertArrayEquals(publicKey.getEncoded(), publicKey1.getEncoded());
    Assertions.assertEquals(publicKey.getFormat(), publicKey1.getFormat());
    Assertions.assertEquals(publicKey.getAlgorithm(), publicKey1.getAlgorithm());
  }

  @Test
  void pemClasspathReaderAndWriter_privateKey() throws PEMException {
    String name = RandomUtil.randomString(8);
    ASN1PrivateKeyReader asn1Reader = new PemClasspathReaderAndWriter(name);
    Assertions.assertThrows(PEMException.class, () -> SecretKeyUtil.readPrivateKey(asn1Reader));
    ASN1PrivateKeyWriter asn1Writer = new PemClasspathReaderAndWriter(name);
    SecretKeyUtil.writePrivateKey(privateKey, asn1Writer);
    Assertions.assertTrue(ResourceUtil.getResourceFromClasspath(name + ".secret.pem")
        .toFile().exists()
    );
    PrivateKey publicKey1 = SecretKeyUtil.readPrivateKey(asn1Reader);
    Assertions.assertArrayEquals(privateKey.getEncoded(), publicKey1.getEncoded());
    Assertions.assertEquals(privateKey.getFormat(), publicKey1.getFormat());
    Assertions.assertEquals(privateKey.getAlgorithm(), publicKey1.getAlgorithm());
  }
}
