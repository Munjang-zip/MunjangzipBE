package com.backend.Gdg.global.domain.entity;

import com.backend.Gdg.global.domain.enums.Gender;
import com.backend.Gdg.global.domain.enums.OAuth2Provider;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(length = 255, nullable = false)
    private String nickName;

    @Column(length = 255, nullable = false)
    private String libraryName;

    @Column(length = 255, nullable = false)
    private String characterName;

    @Column(length = 255, nullable = false)
    private String character;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(length = 255)
    private String accessToken;

    @Column(length = 255)
    private String refreshToken;

    @Column(length = 255, nullable = false, unique = true)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

    private Long age;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    private OAuth2Provider provider;

    @Column(length = 255)
    private String providerId;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Category> categories = new ArrayList<>();

    public void updateToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public void updateProfile(String nickname, String libraryName, String character, String characterName) {
        this.nickName = nickname;
        this.libraryName = libraryName;
        this.character = character;
        this.characterName = characterName;
    }
}