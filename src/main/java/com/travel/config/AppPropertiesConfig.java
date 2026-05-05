package com.travel.config;

import com.travel.security.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, OssProperties.class})
public class AppPropertiesConfig {
}
