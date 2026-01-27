package movieApp.security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Configuration
    public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
            final String securitySchemeName = "bearerAuth";

            return new OpenAPI()
                    .info(new Info()
                            .title("Movie App API")
                            .version("1.0")
                            .description("API for managing movies"))
                    .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                    .components(new Components()
                            .addSecuritySchemes(securitySchemeName,
                                    new SecurityScheme()
                                            .type(SecurityScheme.Type.HTTP)
                                            .scheme("bearer")
                                            .bearerFormat("JWT")
                                            .name("Authorization")
                                            .description("Enter the JWT token. Format: Bearer {token}")));
        }

        @Bean
        public GroupedOpenApi publicApi() {
            return GroupedOpenApi.builder()
                    .group("public")
                    .pathsToMatch("/security/jwt", "/security/registration", "/debug/**")
                    .build();
        }

        @Bean
        public GroupedOpenApi protectedApi() {
            return GroupedOpenApi.builder()
                    .group("protected")
                    .pathsToMatch("/**")
                    .pathsToExclude("/security/jwt", "/security/registration", "/swagger-ui/**", "/v3/api-docs/**")
                    .addOpenApiCustomizer(openApi ->
                            openApi.addSecurityItem(new SecurityRequirement().addList("bearerAuth")))
                    .build();
        }
    }
}
