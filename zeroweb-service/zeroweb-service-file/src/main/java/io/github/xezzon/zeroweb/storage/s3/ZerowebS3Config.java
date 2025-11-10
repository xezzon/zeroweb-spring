package io.github.xezzon.zeroweb.storage.s3;

import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.common.config.ZerowebFileConfig;
import io.github.xezzon.zeroweb.common.exception.UnsupportedFileProviderException;
import java.net.URI;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.S3Presigner.Builder;

/// @author xezzon
@Setter
@Configuration
@ConfigurationProperties(prefix = ZerowebS3Config.PREFIX)
@ConditionalOnProperty(name = "S3_BUCKET")
public class ZerowebS3Config {

  static final String PREFIX = ZerowebFileConfig.PREFIX + ".s3";

  private URI endpoint;
  private String accessKey;
  private String secretKey;
  private String region;
  @Getter
  private String bucket;
  /// 单个分片最大大小
  ///
  /// 大于该大小的需要进行分片上传
  ///
  /// 单位 MB
  private Integer partSize = 5;

  public int getPartSize() {
    return this.partSize * 1024 * 1024;
  }

  @Bean
  AwsCredentialsProvider credentialsProvider() {
    return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
  }

  @Bean
  S3Presigner s3Presigner(AwsCredentialsProvider credentialsProvider) {
    Builder builder = S3Presigner.builder();
    if (endpoint != null) {
      builder.endpointOverride(endpoint);
    }
    return builder
        .credentialsProvider(credentialsProvider)
        .region(Region.of(region))
        .serviceConfiguration(S3Configuration.builder()
            .pathStyleAccessEnabled(true)
            .build()
        )
        .build();
  }

  @Bean
  S3Client s3Client(AwsCredentialsProvider credentialsProvider) {
    S3ClientBuilder builder = S3Client.builder();
    if (endpoint != null) {
      builder.endpointOverride(endpoint);
    }
    return builder
        .credentialsProvider(credentialsProvider)
        .region(Region.of(region))
        .serviceConfiguration(S3Configuration.builder()
            .pathStyleAccessEnabled(true)
            .build()
        )
        .build();
  }
}

@Component
@ConditionalOnProperty(prefix = ZerowebFileConfig.PREFIX, name = "provider", havingValue = "S3")
@SuppressWarnings("unused")
class S3ConfigValidator {

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  S3ConfigValidator(Optional<ZerowebS3Config> s3Config) {
    s3Config.orElseThrow(() -> new UnsupportedFileProviderException(FileProviderEnum.S3));
  }
}
