package io.github.xezzon.zeroweb;

import feign.Headers;
import feign.RequestLine;

/**
 * @author xezzon
 */
public interface TestApi {

  @RequestLine("POST /test")
  @Headers("Content-Type: application/json")
  String test(Entity entity);
}
