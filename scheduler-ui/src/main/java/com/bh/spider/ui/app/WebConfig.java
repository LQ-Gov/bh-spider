package com.bh.spider.ui.app;

import com.bh.spider.client.Client;
import com.bh.spider.transfer.Json;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.DispatcherServlet;
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

    //    @Bean
//    public DispatcherServlet dispatcherServlet() {
//        return new DispatcherServlet();
//    }
//
//    @Bean
//    public ServletRegistrationBean dispatcherServletRegistration() {
//        ServletRegistrationBean<DispatcherServlet> registration = new ServletRegistrationBean<>(
//                dispatcherServlet(), "/api/");
//        registration.setName(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME);
//        return registration;
//    }

    @Bean
    @Primary
    public ObjectMapper mapper(Jackson2ObjectMapperBuilder builder){
        return Json.get();
    }

    @Bean(value = "bh-client", initMethod = "open")
    public Client client() {
        return new Client("127.0.0.1:8033");
    }
}
