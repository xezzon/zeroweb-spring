package io.github.xezzon.geom.core.crypto;

import java.io.IOException;

/**
 * @author xezzon
 */
public interface ASN1PrivateKeyReader {

  /**
   * 读取私钥形式的ASN.1
   * @return ASN.1
   * @throws IOException 读取密钥失败
   */
  Object readPrivateKey() throws IOException;
}
