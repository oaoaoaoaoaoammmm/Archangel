package com.tech.afa.archangel.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.archangel")
public class ArchangelProperties {
    private boolean enabled;
}
