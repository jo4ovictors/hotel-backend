package br.edu.ifmg.hotelbao.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("HotelBAO API")
                .version("1.0")
                .license(new License().name("Apache 2.0").url("https://www.ifmg.edu.br"))
                .description("Hotel booking and management API for HotelBao system.")
                .contact(new Contact().name("HotelBao Dev Team").email("support@hotelbao.com")));
    }

}
