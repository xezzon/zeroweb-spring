package io.github.xezzon.zeroweb.third_party_app.auth;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author xezzon
 */
public interface ThirdPartyAppMemberRepository extends JpaRepository<ThirdPartyAppMember, String> {

}
