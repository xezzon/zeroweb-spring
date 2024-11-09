package io.github.xezzon.geom.core.crypto;

import java.io.IOException;

/**
 * @author xezzon
 */
public interface ASN1PublicKeyWriter {

  /**
   * 将ASN.1形式的公钥写入
   * @param publicKey 公钥
   * @throws IOException 写入密钥失败
   */
  void writePublicKey(byte[] publicKey) throws IOException;
}
