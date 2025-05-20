package com.backend.Gdg.global.service.MemberService.oauth;

import com.backend.Gdg.global.apiPayload.code.status.ErrorStatus;
import com.backend.Gdg.global.apiPayload.exception.AuthException;
import com.backend.Gdg.global.web.dto.Member.AuthRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthClient {

    @Value("${kakao.admin-key}")
    private String kakaoAdminKey;

    private final RestTemplate restTemplate;

    public AuthRequestDTO.KakaoUserInfo getUserInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            Long kakaoId = ((Number) body.get("id")).longValue();
            Map<String, Object> account = (Map<String, Object>) body.get("kakao_account");
            String email = (String) account.get("email");
            String nickname = (String) ((Map<String, Object>) account.get("profile")).get("nickname");

            return AuthRequestDTO.KakaoUserInfo.builder()
                    .providerId(kakaoId.toString())
                    .email(email)
                    .nickname(nickname)
                    .build();

        } catch (HttpClientErrorException.Unauthorized e) {
            log.warn("카카오 access token이 유효하지 않습니다. token={}", accessToken);
            throw new AuthException(ErrorStatus.AUTH_INVALID_TOKEN);
        } catch (Exception e) {
            log.error("카카오 사용자 정보 조회 중 예외 발생", e);
            throw new AuthException(ErrorStatus.AUTH_INVALID_TOKEN);
        }
    }

    public void unlink(String kakaoUserId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoAdminKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", "user_id");
        params.add("target_id", kakaoUserId);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        restTemplate.postForEntity("https://kapi.kakao.com/v1/user/unlink", entity, String.class);
    }
}