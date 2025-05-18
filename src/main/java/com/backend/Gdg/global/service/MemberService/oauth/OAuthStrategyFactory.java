package com.backend.Gdg.global.service.MemberService.oauth;

import com.backend.Gdg.global.domain.enums.OAuth2Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OAuthStrategyFactory {

    private final List<OAuthStrategy> strategies;

    public OAuthStrategy getStrategy(OAuth2Provider providerType) {
        return strategies.stream()
                .filter(strategy -> strategy.getProviderType() == providerType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 소셜 로그인 방식입니다."));
    }
}
