package com.backend.Gdg.global.converter;

import com.backend.Gdg.global.domain.entity.Member;
import com.backend.Gdg.global.web.dto.Member.AuthResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class MemberConverter {

    public static AuthResponseDTO.EmailLoginResponse toEmailLoginResponse(String accessToken, String refreshToken, Member member) {
        return AuthResponseDTO.EmailLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(member.getMemberId())
                .build();
    }

    public static AuthResponseDTO.TokenRefreshResponse toTokenRefreshResponse(
            String accessToken, String refreshToken) {
        return AuthResponseDTO.TokenRefreshResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static AuthResponseDTO.UserProfileResponse toUserProfile(Member member) {
        return AuthResponseDTO.UserProfileResponse.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickName())
                .libraryName(member.getLibraryName())
                .pet(member.getCharacterType())
                .petName(member.getCharacterName())
                .build();
    }
}
