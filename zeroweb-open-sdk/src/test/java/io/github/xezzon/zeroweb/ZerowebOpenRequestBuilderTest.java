package io.github.xezzon.zeroweb;

import static io.github.xezzon.zeroweb.TestApplication.SECRET_KEY;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ZerowebOpenRequestBuilderTest {

  @LocalServerPort
  private int port;

  @Test
  void post() {
    TestApi testApi = new RequestBuilder("hello", SECRET_KEY)
        .target(TestApi.class, "http://localhost:" + this.port);
    Entity entity = new Entity("Alice");
    String response = testApi.post(entity);
    Assertions.assertEquals("Hello, Alice", response);
  }

  @Test
  void get() {
    TestApi testApi = new RequestBuilder("hello", SECRET_KEY)
        .target(TestApi.class, "http://localhost:" + this.port);
    String response = testApi.get();
    Assertions.assertEquals("Hello, Alice", response);
  }
}
