package com.authserver.auth.oauth;

import com.authserver.common.enums.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class OAuth2Provider {

    private String email;
    private String name;
    private String imageUrl;
    private Provider provider;
    private String userNameAttributeName;
    private Map<String, Object> attributes;

    public static OAuth2Provider of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "google" -> ofGoogle(userNameAttributeName, attributes);
            case "kakao" -> ofKakao(userNameAttributeName, attributes);
            case "naver" -> ofNaver(userNameAttributeName, attributes);
            default -> null;
        };
    }

    private static OAuth2Provider ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Provider.builder()
                .email((String) attributes.get("email"))
                .name((String) attributes.get("name"))
                .imageUrl((String) attributes.get("picture"))
                .provider(Provider.GOOGLE)
                .userNameAttributeName(userNameAttributeName)
                .attributes(attributes)
                .build();
    }

    private static OAuth2Provider ofKakao(String userNameAttributeName, Map<String ,Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuth2Provider.builder()
                .email((String) kakaoAccount.get("email"))
                .name((String) kakaoProfile.get("nickname"))
                .imageUrl((String) kakaoProfile.get("profile_img_url"))
                .provider(Provider.KAKAO)
                .userNameAttributeName(userNameAttributeName)
                .attributes(attributes)
                .build();
    }

    private static OAuth2Provider ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return OAuth2Provider.builder()
                .email((String) response.get("email"))
                .name((String) response.get("name"))
                .imageUrl((String) response.get("profile"))
                .provider(Provider.NAVER)
                .userNameAttributeName(userNameAttributeName)
                .attributes(attributes)
                .build();
    }

    private OAuth2Provider() {
        super();
    }
}