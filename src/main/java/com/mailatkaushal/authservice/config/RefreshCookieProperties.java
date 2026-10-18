package com.mailatkaushal.authservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration 
@ConfigurationProperties(prefix="authservice.cookie.refresh-token")
@Getter 
@Setter 
public class RefreshCookieProperties {
    private String path;
    private boolean httpOnly;
    private boolean secure;
    private String sameSite;
}
