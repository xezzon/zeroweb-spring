package io.github.xezzon.zeroweb.core.crypto;

import java.io.IOException;

/**
 * @author xezzon
 */
public interface ASN1PublicKeyReader {

  /**
   * 读取公钥形式的ASN.1
   * @return ASN.1
   * @throws IOException 读取密钥失败
   */
  Object readPublicKey() throws IOException;
}
