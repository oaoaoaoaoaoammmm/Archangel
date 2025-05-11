package com.tech.afa.archangel.configuration;

import com.tech.afa.archangel.library.config.TriggerMode;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.archangel")
public class ArchangelProperties {
    private boolean enabled;
    private String schema;
    private long delayToRefreshSchemaSec = 600;
    private long triggerThreshold = 1;
    private TriggerMode triggerMode = TriggerMode.BY_COUNT;
}
