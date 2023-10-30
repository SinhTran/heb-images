package com.heb.image.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


@ConfigurationProperties(prefix = "imagga")
@ConfigurationPropertiesScan
@Data
public class ImaggaProperties {

    private String apiKey;
    private String apiSecret;
    private String basePath;
}
