package com.tech.afa.archangel.configuration;

import com.tech.afa.archangel.library.Archangel;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceWrapperConfiguration {

    @Bean
    @LiquibaseDataSource
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    @Primary
    public DataSource dataSourceWrapper(DataSource dataSource, ArchangelProperties properties) {
        return new Archangel(dataSource, properties.getSchema()).getWrapperDataSource();
    }
}