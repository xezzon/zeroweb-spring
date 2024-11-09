package io.github.xezzon.geom.core.crypto;

import java.io.IOException;

/**
 * @author xezzon
 */
public interface ASN1PrivateKeyWriter {

  /**
   * 将ASN.1形式的私钥写入
   * @param privateKey 私钥
   * @throws IOException 写入密钥失败
   */
  void writePrivateKey(byte[] privateKey) throws IOException;
}
