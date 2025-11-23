package io.github.xezzon.zeroweb.storage.s3.repository;

import io.github.xezzon.zeroweb.storage.s3.entity.S3UploadId;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
@NullMarked
public interface S3UploadIdRepository extends
    JpaRepository<S3UploadId, String>,
    JpaSpecificationExecutor<S3UploadId> {

}
