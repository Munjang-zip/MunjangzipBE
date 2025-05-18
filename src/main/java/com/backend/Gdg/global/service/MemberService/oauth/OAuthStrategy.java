package com.backend.Gdg.global.service.MemberService.oauth;

import com.backend.Gdg.global.domain.enums.OAuth2Provider;
import com.backend.Gdg.global.web.dto.Member.AuthResponseDTO;

public interface OAuthStrategy {
    AuthResponseDTO.OAuthResponse login(String token);
    void unlink(String providerId);
    OAuth2Provider getProviderType();
}