package io.github.xezzon.zeroweb.attachment.repository;

import io.github.xezzon.zeroweb.attachment.Attachment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/// @author xezzon
@Repository
public interface AttachmentRepository extends
    JpaRepository<Attachment, String>,
    JpaSpecificationExecutor<Attachment> {

  List<Attachment> findByBizTypeAndBizId(String bizType, String bizId);
}
