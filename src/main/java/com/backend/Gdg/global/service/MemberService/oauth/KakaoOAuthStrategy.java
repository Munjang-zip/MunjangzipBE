package com.backend.Gdg.global.service.MemberService.oauth;

import com.backend.Gdg.global.domain.entity.Member;
import com.backend.Gdg.global.domain.enums.OAuth2Provider;
import com.backend.Gdg.global.repository.MemberRepository;
import com.backend.Gdg.global.security.provider.JwtTokenProvider;
import com.backend.Gdg.global.web.dto.Member.AuthRequestDTO;
import com.backend.Gdg.global.web.dto.Member.AuthResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoOAuthStrategy implements OAuthStrategy {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponseDTO.OAuthResponse login(String kakaoAccessToken) {
        AuthRequestDTO.KakaoUserInfo userInfo = kakaoOAuthClient.getUserInfo(kakaoAccessToken);

        Member member = memberRepository
                .findByProviderAndProviderId(OAuth2Provider.KAKAO, userInfo.getProviderId())
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .email(userInfo.getEmail())
                        .nickName(userInfo.getNickname())
                        .provider(OAuth2Provider.KAKAO)
                        .providerId(userInfo.getProviderId())
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
        kakaoOAuthClient.unlink(providerId);
    }

    @Override
    public OAuth2Provider getProviderType() {
        return OAuth2Provider.KAKAO;
    }
}
