package io.github.xezzon.zeroweb.common.grpc;

import io.grpc.Metadata.BinaryMarshaller;
import java.nio.charset.StandardCharsets;

/**
 * @author xezzon
 */
public class Utf8Marshaller implements BinaryMarshaller<String> {

  @Override
  public byte[] toBytes(final String s) {
    if (s == null) {
      return new byte[0];
    }
    return s.getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public String parseBytes(final byte[] bytes) {
    if (bytes == null || bytes.length == 0) {
      return "";
    }
    return new String(bytes, StandardCharsets.UTF_8);
  }
}
