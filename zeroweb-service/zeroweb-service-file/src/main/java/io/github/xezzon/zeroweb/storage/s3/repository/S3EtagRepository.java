package io.github.xezzon.zeroweb.storage.s3.repository;

import io.github.xezzon.zeroweb.storage.s3.entity.S3Etag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public interface S3EtagRepository extends
    JpaRepository<S3Etag, String>,
    JpaSpecificationExecutor<S3Etag> {

  List<S3Etag> findByAttachmentIdOrderByPartNumberAsc(String attachmentId);

  Optional<S3Etag> findByAttachmentIdAndPartNumber(String attachmentId, Integer partNumber);
}
