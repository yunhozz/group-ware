package com.authserver.auth.oauth;

import com.authserver.auth.session.UserPrincipal;
import com.authserver.common.enums.Role;
import com.authserver.common.util.RandomIdUtils;
import com.authserver.persistence.User;
import com.authserver.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OAuth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final RandomIdUtils randomIdUtils;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2Provider provider = OAuth2Provider.of(registrationId, userNameAttributeName, attributes);
        User user = saveOrUpdateUser(provider);

        return new UserPrincipal(user, attributes);
    }

    private User saveOrUpdateUser(OAuth2Provider provider) {
        User[] user = {null};
        userRepository.findByEmail(provider.getEmail()).ifPresentOrElse(u -> {
            user[0] = u;
            u.updateByProvider(provider.getName(), provider.getImageUrl(), provider.getProvider());
        }, () -> {
            user[0] = buildUser(provider);
            userRepository.save(user[0]);
        });

        return user[0];
    }

    private User buildUser(OAuth2Provider provider) {
        return User.builder()
                .userId(randomIdUtils.generateUserId())
                .email(provider.getEmail())
                .password(null)
                .name(provider.getName())
                .imageUrl(provider.getImageUrl())
                .provider(provider.getProvider())
                .roles(Set.of(Role.GUEST))
                .build();
    }
}