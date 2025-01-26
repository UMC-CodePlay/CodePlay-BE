package umc.codeplay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI CodePlayAPI() {

        Info info =
                new Info()
                        .title("CodePlay Server API")
                        .description(
                                "<div>UMC 7th Code Play Server API 문서.</div>"
                                        + "<div>자세한 작동 방식 등은 Front-End 태그가 담긴 discussion 확인 부탁드립니다.</div>"
                                        + "https://github.com/UMC-CodePlay/CodePlay-BE/discussions")
                        .version("1.0");

        String securitySchemeName = "JWT TOKEN";

        SecurityRequirement securityRequirement =
                new SecurityRequirement().addList(securitySchemeName);

        Components components =
                new Components()
                        .addSecuritySchemes(
                                securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"));

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
