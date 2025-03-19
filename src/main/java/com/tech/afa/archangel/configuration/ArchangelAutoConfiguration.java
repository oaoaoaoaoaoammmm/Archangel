package com.tech.afa.archangel.configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan(basePackages = "com.tech.afa.archangel")
@EnableConfigurationProperties(ArchangelProperties.class)
@ConditionalOnProperty(prefix = "spring.archangel", name = "enabled", havingValue = "true", matchIfMissing = false)
public class ArchangelAutoConfiguration {

    @PostConstruct
    public void archangelStart() {
        log.info("Archangel is active!");
    }
}
