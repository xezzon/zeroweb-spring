package io.github.xezzon.geom;

import static io.github.xezzon.geom.TestApplication.SECRET_KEY;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class GeomOpenRequestBuilderTest {

  @LocalServerPort
  private int port;

  @Test
  void test() {
    TestApi testApi = new RequestBuilder("hello", SECRET_KEY).builder()
        .target(TestApi.class, "http://localhost:" + this.port);
    Entity entity = new Entity("Alice");
    String response = testApi.test(entity);
    Assertions.assertEquals("Hello, Alice", response);
  }
}
