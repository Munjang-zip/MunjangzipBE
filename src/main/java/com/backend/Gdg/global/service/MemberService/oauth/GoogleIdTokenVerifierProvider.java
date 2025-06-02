package com.backend.Gdg.global.service.MemberService.oauth;

import com.backend.Gdg.global.apiPayload.code.status.ErrorStatus;
import com.backend.Gdg.global.apiPayload.exception.AuthException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
public class GoogleIdTokenVerifierProvider {

    //구글 웹앱 ClinetId
    @Value("${google.client-id}")
    private String clientId;

    private GoogleIdTokenVerifier verifier;

    @PostConstruct
    public void init() {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    /**
     * Google의 idToken을 검증하고 Payload를 반환합니다.
     * @param idTokenString 클라이언트가 전달한 idToken (JWT)
     * @return GoogleIdToken.Payload 객체 (검증에 통과하면 유효)
     */
    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                return idToken.getPayload();
            } else {
                log.warn("Google ID Token 검증 실패: 유효하지 않음");
                throw new AuthException(ErrorStatus.AUTH_INVALID_TOKEN);
            }
        } catch (Exception e) {
            log.error("Google ID Token 검증 중 예외 발생: {}", e.getMessage());
            throw new AuthException(ErrorStatus.AUTH_INVALID_TOKEN);
        }
    }
}