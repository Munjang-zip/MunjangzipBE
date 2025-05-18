package com.backend.Gdg.global.service.MemberService;


import com.backend.Gdg.global.apiPayload.code.status.ErrorStatus;
import com.backend.Gdg.global.apiPayload.exception.AuthException;
import com.backend.Gdg.global.apiPayload.exception.MemberException;
import com.backend.Gdg.global.converter.MemberConverter;
import com.backend.Gdg.global.domain.entity.Member;
import com.backend.Gdg.global.domain.enums.OAuth2Provider;
import com.backend.Gdg.global.repository.MemberRepository;
import com.backend.Gdg.global.security.provider.JwtTokenProvider;
import com.backend.Gdg.global.web.dto.Member.AuthRequestDTO;
import com.backend.Gdg.global.web.dto.Member.AuthResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService{

    @Value("${kakao.admin-key}")
    private String kakaoAdminKey;

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;

    @Override
    public Member findMemberById(Long memberId) {
        return memberRepository
                .findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    @Override
    @Transactional
    public void emailRegister(AuthRequestDTO.EmailRegisterRequest request) {
        // 이메일 중복 검사
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new MemberException(ErrorStatus.MEMBER_EMAIL_ALREADY_EXISTS);
        }

        // 회원 생성
        Member member = Member.builder()
                .nickName(request.getNickname())
                .libraryName(request.getLibraryName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        memberRepository.save(member);
    }


    @Override
    @Transactional
    public AuthResponseDTO.EmailLoginResponse emailLogin(AuthRequestDTO.EmailLoginRequest request) {
        // 이메일로 사용자 조회
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_EMAIL_ALREADY_EXISTS));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new MemberException(ErrorStatus.MEMBER_LOGIN_FAIL);
        }

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(member.getMemberId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getMemberId());

        // DB에 refresh token 저장
        member.setRefreshToken(refreshToken);
        member.setAccessToken(accessToken);
        memberRepository.save(member);


        return MemberConverter.toEmailLoginResponse(accessToken, refreshToken, member);
    }

    @Override
    @Transactional
    public AuthResponseDTO.TokenRefreshResponse refreshToken(String refreshToken) {
        jwtTokenProvider.isTokenValid(refreshToken);

        Long id = jwtTokenProvider.getId(refreshToken);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        if (!refreshToken.equals(member.getRefreshToken())) {
            throw new AuthException(ErrorStatus.AUTH_INVALID_TOKEN);
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(id);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(id);
        member.updateToken(newAccessToken, newRefreshToken);
        memberRepository.save(member);
        return MemberConverter.toTokenRefreshResponse(newAccessToken, newRefreshToken);
    }


    public AuthResponseDTO.OAuthResponse loginWithKakaoAccessToken(String kakaoAccessToken) {
        // 1. 카카오 API 호출
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + kakaoAccessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                entity,
                Map.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new AuthException(ErrorStatus.AUTH_INVALID_TOKEN);
        }

        // 2. 사용자 정보 추출
        Map<String, Object> body = response.getBody();
        Long kakaoId = ((Number) body.get("id")).longValue();

        Map<String, Object> account = (Map<String, Object>) body.get("kakao_account");
        String email = (String) account.get("email");
        String nickname = (String) ((Map<String, Object>) account.get("profile")).get("nickname");

        // 3. DB 조회 or 회원가입
        Member member = memberRepository.findByProviderAndProviderId(OAuth2Provider.KAKAO.name(), kakaoId.toString())
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .email(email)
                        .nickName(nickname)
                        .provider(OAuth2Provider.KAKAO.name())
                        .providerId(kakaoId.toString())
                        .build()));

        // 4. JWT 발급
        String accessToken = jwtTokenProvider.createAccessToken(member.getMemberId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getMemberId());


        return AuthResponseDTO.OAuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(member.getMemberId())
                .build();
    }

    @Override
    @Transactional
    public void logout(String accessToken) {
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        Long memberId = jwtTokenProvider.getId(accessToken);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        member.updateToken(null, null);
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void withdraw(String accessToken) {
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        Long memberId = jwtTokenProvider.getId(accessToken);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        // 카카오 unlink API 호출
        if ("KAKAO".equalsIgnoreCase(member.getProvider())) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoAdminKey); // REST API 키 X, Admin 키
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("target_id_type", "user_id");
            params.add("target_id", member.getProviderId());

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
            restTemplate.postForEntity("https://kapi.kakao.com/v1/user/unlink", entity, String.class);
        }

        // 실제 서비스에서는 soft-delete 고려 가능
        memberRepository.delete(member);
    }





    // 랜덤 닉네임 생성
    private String makeNickname(){
        List<String> determiners = List.of(
                "예쁜", "멋진", "귀여운", "배고픈", "철학적인", "현학적인", "슬픈", "파란", "비싼", "밝은", "생각하는", "하얀"
        );

        List<String> animals = List.of(
                "토끼", "비버", "강아지", "부엉이", "여우", "호랑이", "문어", "고양이", "미어캣", "다람쥐", "수달", "곰"
        );

        Random random = new Random();
        String determiner = determiners.get(random.nextInt(determiners.size()));
        String animal = animals.get(random.nextInt(animals.size()));
        return determiner + " " + animal;
    }
}