package com.carhub.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "com.carhub")
@PropertySource("classpath:application.properties")
public class ApplicationConfig {
    
}
