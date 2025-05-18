package com.backend.Gdg.global.web.controller;

import com.backend.Gdg.global.apiPayload.code.status.SuccessStatus;
import com.backend.Gdg.global.apiPayload.ApiResponse;
import com.backend.Gdg.global.domain.enums.OAuth2Provider;
import com.backend.Gdg.global.service.MemberService.MemberCommandService;
import com.backend.Gdg.global.web.dto.Member.AuthRequestDTO;
import com.backend.Gdg.global.web.dto.Member.AuthResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@CrossOrigin
@Slf4j
@RequestMapping("/member")
@Tag(name = "회원 API", description = "회원 관련 API입니다.")
public class MemberController {
    private final MemberCommandService memberService;

    // 이메일 회원가입 API
    @PostMapping("/register")
    @Operation(summary = "이메일 회원가입 API", description = "이메일 회원가입을 진행하는 API 입니다.")
    public ApiResponse<?> emailSignUp(@RequestBody @Valid AuthRequestDTO.EmailRegisterRequest request){
        memberService.emailRegister(request);
        return ApiResponse.onSuccess(SuccessStatus.MEMBER_OK, null);
    }

    // 이메일 로그인 API
    @PostMapping("/login/email")
    @Operation(summary = "이메일 로그인 API", description = "이메일과 비밀번호로 로그인을 진행하는 API 입니다.")
    public ApiResponse<AuthResponseDTO.EmailLoginResponse> emailLogin(@RequestBody @Valid AuthRequestDTO.EmailLoginRequest request){
        AuthResponseDTO.EmailLoginResponse response = memberService.emailLogin(request);
        return ApiResponse.onSuccess(SuccessStatus.MEMBER_OK, response);
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "JWT Access Token 재발급 API",
            description = "Refresh Token을 검증하고 새로운 Access Token과 Refresh Token을 응답합니다.")
    public ApiResponse<AuthResponseDTO.TokenRefreshResponse> refresh(@RequestBody AuthRequestDTO.RefreshToken request) {
        AuthResponseDTO.TokenRefreshResponse response = memberService.refreshToken(request.getRefreshToken());
        return ApiResponse.onSuccess(SuccessStatus.MEMBER_OK, response);
    }

    @PostMapping("/oauth/login")
    @Operation(summary = "소셜 로그인", description = "Google 또는 Kakao 소셜 로그인")
    public ApiResponse<AuthResponseDTO.OAuthResponse> oauthLogin(
            @RequestParam("provider") OAuth2Provider provider,
            @RequestParam("token") String token
    ) {
        return ApiResponse.onSuccess(SuccessStatus.MEMBER_OK,memberService.loginWithOAuth(provider, token));
    }

    @PostMapping("/token/refresh")
    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 AccessToken 재발급")
    public ApiResponse<AuthResponseDTO.TokenRefreshResponse> refreshToken(
            @RequestParam("refreshToken") String refreshToken
    ) {
        return ApiResponse.onSuccess(SuccessStatus.MEMBER_OK,memberService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "AccessToken으로 로그아웃")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String accessToken) {
        memberService.logout(accessToken);
        return ApiResponse.onSuccess(SuccessStatus.MEMBER_OK,null);
    }

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 처리")
    public ApiResponse<Void> withdraw(@RequestHeader("Authorization") String accessToken) {
        memberService.withdraw(accessToken);
        return ApiResponse.onSuccess(SuccessStatus.MEMBER_OK,null);
    }
}
