package com.bh.spider.ui.app;

import com.bh.spider.client.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/public/**.html").addResourceLocations("/public/");
        registry.addResourceHandler("/assets/css/*.css").addResourceLocations("/public/assets/css/");
        registry.addResourceHandler("/*.js").addResourceLocations("/public/");
    }

    @Bean(value = "bh-client",initMethod = "open")
    public Client client() {
        return new Client("127.0.0.1:8033");
    }
}
