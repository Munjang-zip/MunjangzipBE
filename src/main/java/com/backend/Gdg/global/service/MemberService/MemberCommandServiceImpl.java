package com.backend.Gdg.global.service.MemberService;


import com.backend.Gdg.global.apiPayload.code.status.ErrorStatus;
import com.backend.Gdg.global.apiPayload.exception.AuthException;
import com.backend.Gdg.global.apiPayload.exception.MemberException;
import com.backend.Gdg.global.converter.MemberConverter;
import com.backend.Gdg.global.domain.entity.Member;
import com.backend.Gdg.global.domain.enums.OAuth2Provider;
import com.backend.Gdg.global.repository.MemberRepository;
import com.backend.Gdg.global.security.provider.JwtTokenProvider;
import com.backend.Gdg.global.service.MemberService.oauth.OAuthStrategy;
import com.backend.Gdg.global.service.MemberService.oauth.OAuthStrategyFactory;
import com.backend.Gdg.global.web.dto.Member.AuthRequestDTO;
import com.backend.Gdg.global.web.dto.Member.AuthResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandServiceImpl implements MemberCommandService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuthStrategyFactory oAuthStrategyFactory;
    private final MemberConverter memberConverter;

    @Override
    @Transactional(readOnly = true)
    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    @Override
    public void emailRegister(AuthRequestDTO.EmailRegisterRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new MemberException(ErrorStatus.MEMBER_EMAIL_ALREADY_EXISTS);
        }

        Member member = Member.builder()
                .nickName(request.getNickname())
                .libraryName(request.getLibraryName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        memberRepository.save(member);
    }

    @Override
    public AuthResponseDTO.EmailLoginResponse emailLogin(AuthRequestDTO.EmailLoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_EMAIL_ALREADY_EXISTS));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new MemberException(ErrorStatus.MEMBER_LOGIN_FAIL);
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getMemberId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getMemberId());
        member.updateToken(accessToken, refreshToken);
        memberRepository.save(member);

        return MemberConverter.toEmailLoginResponse(accessToken, refreshToken, member);
    }

    @Override
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

    @Override
    public AuthResponseDTO.OAuthResponse loginWithOAuth(OAuth2Provider provider, String token) {
        OAuthStrategy strategy = oAuthStrategyFactory.getStrategy(provider);
        return strategy.login(token);
    }

    @Override
    public void logout(Member member) {
        member.updateToken(null, null);
        memberRepository.save(member);
    }

    @Override
    public void withdraw(Member member) {
        OAuthStrategy strategy = oAuthStrategyFactory.getStrategy(
                OAuth2Provider.valueOf(member.getProvider().name()));
        strategy.unlink(member.getProviderId());

        memberRepository.delete(member);
    }

    @Override
    public AuthResponseDTO.UserProfileResponse registerUserProfile(Member member, AuthRequestDTO.UserProfile request) {
        member.updateProfile(
                request.getNickname(),
                request.getLibraryName(),
                request.getCharacter(),
                request.getCharacterName()
        );

        return memberConverter.toUserProfile(member);
    }

}