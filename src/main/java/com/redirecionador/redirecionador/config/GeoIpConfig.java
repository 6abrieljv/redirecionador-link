package com.redirecionador.redirecionador.config;

import com.maxmind.geoip2.DatabaseReader;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class GeoIpConfig {
    private static final Logger logger = LoggerFactory.getLogger(GeoIpConfig.class);

    @Bean
    @ConditionalOnProperty(name = "app.geoip.database")
    public DatabaseReader databaseReader(@Value("${app.geoip.database}") Resource resource) {
        if (!resource.exists()) {
            logger.warn("GeoIP2 database not found: {}", resource);
            return null;
        }
        try {
            return new DatabaseReader.Builder(resource.getFile()).build();
        } catch (IOException ex) {
            logger.warn("Failed to load GeoIP2 database: {}", resource, ex);
            return null;
        }
    }
}
