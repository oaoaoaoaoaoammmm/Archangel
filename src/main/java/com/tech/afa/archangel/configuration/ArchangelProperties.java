package com.tech.afa.archangel.configuration;

import com.tech.afa.archangel.library.config.TriggerMode;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.archangel")
public class ArchangelProperties {
    private boolean enabled;
    private String schema;
    private int triggerThreshold = 1;
    private TriggerMode triggerMode = TriggerMode.BY_COUNT;
}
