package com.thewhite.news.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Vdovin S. on 18.05.18.
 * Конфигурация приложения
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
