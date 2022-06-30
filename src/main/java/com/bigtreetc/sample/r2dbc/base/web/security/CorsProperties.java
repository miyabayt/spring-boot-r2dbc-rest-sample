package com.bigtreetc.sample.r2dbc.base.web.security;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "application.cors")
public class CorsProperties {

  Boolean allowCredentials;

  List<String> allowedHeaders;

  List<String> allowedMethods;

  List<String> allowedOrigins;

  List<String> exposedHeaders;

  Long maxAge;
}
