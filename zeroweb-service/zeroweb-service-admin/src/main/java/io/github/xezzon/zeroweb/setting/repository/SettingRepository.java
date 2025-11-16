package io.github.xezzon.zeroweb.setting.repository;

import io.github.xezzon.zeroweb.setting.Setting;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
@NullMarked
public interface SettingRepository extends
    JpaRepository<Setting, String>,
    JpaSpecificationExecutor<Setting> {

}
