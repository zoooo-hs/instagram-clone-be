package com.zoooohs.instagramclone.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class OpenAPIConfiguration {

    @Bean
    public ModelResolver modelResolver(ObjectMapper objectMapper) {
        return new ModelResolver(objectMapper);
    }

    @Bean
    public OpenAPI openAPI(@Value("${instagram-clone.version}") String version) {
        Info info = new Info().title("Instagram Clone Backend").version(version)
                .description("퇴근하고 뭐라도 해보려고 한다. TDD 공부도 하고 Spring 프로젝트를 계속 손에 익혀본다.")
                .contact(new Contact().name("zoooo-hs (Hyunsu Ju)").url("https://github.com/zoooo-hs").email("dogfooter219@gmail.com"));

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}
