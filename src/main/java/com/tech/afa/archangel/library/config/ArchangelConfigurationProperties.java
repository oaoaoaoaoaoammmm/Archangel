package com.tech.afa.archangel.library.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchangelConfigurationProperties {
    private String schema;
    private long delayToRefreshSchemaSec = 600;
    private long triggerThreshold = 1;
    private TriggerMode triggerMode = TriggerMode.BY_COUNT;
}

