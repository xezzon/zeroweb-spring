package io.github.xezzon.zeroweb.storage;

import lombok.Getter;

/**
 * @author xezzon
 */
@Getter
public class DownloadEndpoint {

  private String endpoint;

  @SuppressWarnings("unused")
  DownloadEndpoint() {
    super();
  }

  public DownloadEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }
}
