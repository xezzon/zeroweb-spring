package io.github.xezzon.geom.common.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author xezzon
 */
class ResourceUtilTest {

  @Test
  void getResourceFromClasspath() throws IOException {
    Path resource = ResourceUtil.getResourceFromClasspath("test.txt");
    byte[] bytes = Files.readAllBytes(resource);
    String string = new String(bytes);
    Assertions.assertEquals("hello\n", string);
  }
}