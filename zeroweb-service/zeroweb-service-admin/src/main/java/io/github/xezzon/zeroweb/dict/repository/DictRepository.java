package io.github.xezzon.zeroweb.dict.repository;

import io.github.xezzon.zeroweb.dict.domain.Dict;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public interface DictRepository extends
    JpaRepository<Dict, String>,
    JpaSpecificationExecutor<Dict> {

  Optional<Dict> findByTagAndCode(String tag, String code);

  List<Dict> findByParentIdIn(Collection<String> parentIds);

  List<Dict> findByTagOrderByOrdinalAsc(String tag);
}
