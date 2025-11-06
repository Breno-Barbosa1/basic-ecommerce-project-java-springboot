package br.com.breno_barbosa1.basic_ecommerce.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Basic Ecommerce Api Project using Java Spring Boot")
                    .version("v1")
                    .description("This API is for a personal study project only. There are no formal terms of service.")
                        .license(new License().name("Apache 2.0"))
                );
    }
}