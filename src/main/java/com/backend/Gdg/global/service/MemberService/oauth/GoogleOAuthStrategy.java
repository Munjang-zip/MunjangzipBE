package com.backend.Gdg.global.service.MemberService.oauth;

import com.backend.Gdg.global.domain.entity.Member;
import com.backend.Gdg.global.domain.enums.OAuth2Provider;
import com.backend.Gdg.global.repository.MemberRepository;
import com.backend.Gdg.global.security.provider.JwtTokenProvider;
import com.backend.Gdg.global.web.dto.Member.AuthResponseDTO;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleOAuthStrategy implements OAuthStrategy {

    private final GoogleIdTokenVerifierProvider googleVerifier;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponseDTO.OAuthResponse login(String idToken) {
        GoogleIdToken.Payload payload = googleVerifier.verify(idToken);
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String providerId = payload.getSubject();

        Member member = memberRepository
                .findByProviderAndProviderId(OAuth2Provider.GOOGLE, providerId)
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .email(email)
                        .nickName(name)
                        .provider(OAuth2Provider.GOOGLE)
                        .providerId(providerId)
                        .build()));

        String accessToken = jwtTokenProvider.createAccessToken(member.getMemberId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getMemberId());
        member.updateToken(accessToken, refreshToken);
        memberRepository.save(member);

        return AuthResponseDTO.OAuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(member.getMemberId())
                .build();
    }

    @Override
    public void unlink(String providerId) {
        // 구글은 특별한 unlink 없음
    }

    @Override
    public OAuth2Provider getProviderType() {
        return OAuth2Provider.GOOGLE;
    }
}