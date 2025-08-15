package io.github.xezzon.zeroweb.third_party_app;

import io.github.xezzon.zeroweb.third_party_app.authn.ThirdPartyAppMember;
import java.util.Optional;

/**
 * @author xezzon
 */
public interface IThirdPartyAppMemberService {

  Optional<ThirdPartyAppMember> queryMember(String groupId, String userId);
}
