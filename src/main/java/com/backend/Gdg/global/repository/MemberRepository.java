package com.backend.Gdg.global.repository;

import com.backend.Gdg.global.domain.entity.Member;
import com.backend.Gdg.global.domain.enums.OAuth2Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<Member> findByNickName(String nickName);
    Optional<Member> findByProviderAndProviderId(OAuth2Provider provider, String providerId);

}
