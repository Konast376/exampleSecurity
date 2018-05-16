package com.thewhite.news;

import com.whitesoft.core.config.CoreMappersConfig;
import com.whitesoft.util.CustomRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * Болванка проекта для микросервисов
 */
@EnableResourceServer
@EnableDiscoveryClient
@SpringBootApplication
@Import(CoreMappersConfig.class)
public class App {
    public static void main(String[] args) {
        CustomRunner.run(App.class, args, SpringApplication::run);
    }
}
