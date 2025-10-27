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
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/// @author xezzon
@Setter
@Configuration
@ConfigurationProperties(prefix = ZerowebS3Config.PREFIX)
@ConditionalOnProperty(name = "S3_ENDPOINT")
public class ZerowebS3Config {

  static final String PREFIX = ZerowebFileConfig.PREFIX + ".s3";

  private String endpoint;
  private String accessKey;
  private String secretKey;
  private String region;
  @Getter
  private String bucket;

  @Bean
  AwsCredentialsProvider credentialsProvider() {
    return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
  }

  @Bean
  S3Presigner s3Presigner(AwsCredentialsProvider credentialsProvider) {
    return S3Presigner.builder()
        .endpointOverride(URI.create(endpoint))
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
    return S3Client.builder()
        .endpointOverride(URI.create(endpoint))
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
