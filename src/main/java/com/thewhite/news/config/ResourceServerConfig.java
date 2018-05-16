package com.thewhite.news.config;


import com.thewhite.news.api.dto.WhoAmiDTO;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;


/**
 * Конфигурация сервера ресурсов
 *
 * @author Maxim Seredkin
 * @version 1.0
 */
@EnableResourceServer
@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf()
            .disable()
            .anonymous()
            .and()
            .authorizeRequests()
            .antMatchers("/").permitAll();
    }

    /**
     * Стратегия получения данных о зарегистрированном пользователе
     */
    @Bean
    public PrincipalExtractor principalExtractor() {
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return map -> mapper.convertValue(map, WhoAmiDTO.class);
    }
}
