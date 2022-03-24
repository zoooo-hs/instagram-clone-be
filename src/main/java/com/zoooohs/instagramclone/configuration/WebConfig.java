package com.zoooohs.instagramclone.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${instagram-clone.cors.allow-url:none}")
    private String allowUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (this.allowUrl.equals("none")) {
            return;
        }
        registry.addMapping("/**")
                .allowedOrigins(allowUrl)
                .allowedMethods("*")
                .maxAge(3000);
    }

}
