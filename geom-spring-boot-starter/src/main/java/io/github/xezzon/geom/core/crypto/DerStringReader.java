package io.github.xezzon.geom.core.crypto;

import java.util.Base64;

/**
 * @author xezzon
 */
public class DerStringReader implements ASN1PublicKeyReader{

  private final Base64.Decoder decoder = Base64.getDecoder();
  private final String derBase64;

  public DerStringReader(String derBase64) {
    this.derBase64 = derBase64;
  }

  @Override
  public Object readPublicKey() {
    return decoder.decode(derBase64);
  }
}
