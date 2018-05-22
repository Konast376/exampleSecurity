package com.thewhite.news.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewhite.news.api.dto.WhoAmiDTO;
import com.thewhite.news.model.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Created by Seredkin M. on 05.12.2017.
 * <p>
 * Реализация сервиса для работы с авторизацией
 *
 * @author Maxim Seredkin
 * @version 1.0.0
 */
@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public UserDetails getCurrentUser() {
        return getUserDetails();
    }

    @Override
    public UUID getCurrentUserId() {
        return Optional.ofNullable(getUserDetails()).map(UserDetails::getId).orElse(null);
    }

    /**
     * Возвращает юзер детайлс
     *
     * @return Юзер детайлс
     */
    private UserDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication instanceof OAuth2Authentication) {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
            ObjectMapper objectMapper = new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
            WhoAmiDTO details = objectMapper.convertValue(oAuth2Authentication.getUserAuthentication().getDetails(),
                                                          WhoAmiDTO.class);
            return UserDetails.builder()
                              .id(details.getUserId())
                              .postName(details.getPostName())
                              .userLogin(details.getUserLogin())
                              .userFirstName(details.getUserFirstName())
                              .userLastName(details.getUserLastName())
                              .userMiddleName(details.getUserMiddleName())
                              .authorities(details.getAuthorities())
                              .build();
        }

        return null;
    }
}
