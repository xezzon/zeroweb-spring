package io.github.xezzon.geom;

import feign.Feign.Builder;
import feign.jackson.JacksonEncoder;

/**
 * @author xezzon
 */
public class RequestBuilder extends GeomOpenRequestBuilder {

  public RequestBuilder(String accessKey, String secretKey) {
    super(accessKey, secretKey);
  }

  @Override
  public Builder builder() {
    return super.builder()
        .encoder(new JacksonEncoder())
        ;
  }
}
