package com.bh.spider.ui.app;

import com.bh.spider.client.Client;
import com.bh.common.utils.Json;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/public/**.html").addResourceLocations("/public/");
        registry.addResourceHandler("/assets/css/*.css").addResourceLocations("/public/assets/css/");
        registry.addResourceHandler("/*.js").addResourceLocations("/public/");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(Json.get()));
    }

    @Bean(value = "bh-client", initMethod = "open")
    public Client client() {
        return new Client("127.0.0.1:8033");
    }
}
