package com.rickmorty.Utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Config {
    @Value("${api.base.url}")
    private String apiBaseUrl;

    @Value("${local.base.url}")
    private String localBaseUrl;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    public boolean isAllowSendImages() {
        return cloudName != null && apiKey != null && apiSecret != null;
    }
}
