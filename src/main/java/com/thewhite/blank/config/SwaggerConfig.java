package com.thewhite.blank.config;

import com.google.common.collect.Sets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * Created by Seredkin M. on 20.12.2017.
 * <p>
 * Конфигурация для Swagger
 *
 * @author Maxim Seredkin
 * @version 1.0.0
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    // A tag.
    public static final String ADMIN_TAG = "Admin";

    // P tag.
    public static final String PUBLIC_TAG = "Public";

    // U tag.
    public static final String USER_TAG = "User";

    // Authorization scheme.
    //// TODO: 27.03.18 Изменить всоответствии с проектом
    public static final String AUTHORIZATION_SCHEME = "spring-blank-scheme";

    // API package.
    //// TODO: 27.03.18 Изменить всоответствии с проектом
    private static final String API_PACKAGE = "com.thewhite.blank.api";

    // Token endpoint.
    private static final String TOKEN_ENDPOINT = "/oauth/token";

    @Bean
    public Docket docket() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2).select()
                                                               .apis(RequestHandlerSelectors.basePackage(API_PACKAGE))
                                                               .paths(PathSelectors.any())
                                                               .build();

        docket.produces(Sets.newHashSet(APPLICATION_JSON_UTF8_VALUE));
        docket.consumes(Sets.newHashSet(APPLICATION_JSON_UTF8_VALUE));
        docket.securitySchemes(Collections.singletonList(scheme()));
        docket.securityContexts(Collections.singletonList(context()));
        docket.useDefaultResponseMessages(false);
        docket.tags(new Tag(ADMIN_TAG, null),
                    new Tag(USER_TAG, null),
                    new Tag(PUBLIC_TAG, null));

        return docket;
    }

    private SecurityScheme scheme() {
        ResourceOwnerPasswordCredentialsGrant grant = new ResourceOwnerPasswordCredentialsGrant(TOKEN_ENDPOINT);

        return new OAuth("AUTHORIZATION_SCHEME",
                         Collections.emptyList(), // NOTE: Default scope used.
                         Collections.singletonList(grant));
    }

    private SecurityContext context() {
        SecurityReference reference = new SecurityReference(AUTHORIZATION_SCHEME,
                                                            new AuthorizationScope[]{}); // NOTE: Default scope used.

        return SecurityContext.builder()
                              .securityReferences(Collections.singletonList(reference))
                              .forPaths(PathSelectors.none())
                              .build();
    }

}

