package com.rickmorty.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

   @Bean
   public OpenAPI customOpenAPI() {
      return new OpenAPI()
              .info(new Info()
                      .title("Rick and Morty API")
                      .version("v1")
                      .description("API para gerenciar personagens, episódios e locais do Rick and Morty"))
              .components(new io.swagger.v3.oas.models.Components()
                      .addSecuritySchemes("BearerAuth",
                              new SecurityScheme()
                                      .name("BearerAuth")
                                      .type(SecurityScheme.Type.HTTP)
                                      .scheme("bearer")
                                      .bearerFormat("JWT")));
   }
}
