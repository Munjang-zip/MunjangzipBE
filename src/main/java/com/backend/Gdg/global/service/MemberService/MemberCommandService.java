package com.backend.Gdg.global.service.MemberService;

import com.backend.Gdg.global.domain.entity.Member;
import com.backend.Gdg.global.domain.enums.OAuth2Provider;
import com.backend.Gdg.global.web.dto.Member.AuthRequestDTO;
import com.backend.Gdg.global.web.dto.Member.AuthResponseDTO;

public interface MemberCommandService {

    Member findMemberById(Long memberId);
    void emailRegister(AuthRequestDTO.EmailRegisterRequest request);
    AuthResponseDTO.EmailLoginResponse emailLogin(AuthRequestDTO.EmailLoginRequest request);
    AuthResponseDTO.TokenRefreshResponse refreshToken(String refreshToken);
    void logout(Member member);
    void withdraw(Member member);

    AuthResponseDTO.OAuthResponse loginWithOAuth(OAuth2Provider provider, String token);

    AuthResponseDTO.UserProfileResponse registerUserProfile(Member member,AuthRequestDTO.UserProfile request);

}