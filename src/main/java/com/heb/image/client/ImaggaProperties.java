package com.heb.image.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "imagga")
@Data
public class ImaggaProperties {

    private String apiKey;
    private String apiSecret;
    private String basePath;
}
