package com.redirecionador.redirecionador.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "click_logs")
public class ClickLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String slug;

    @Column(nullable = false, length = 2048)
    private String originalUrl;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 1024)
    private String userAgent;

    @Column(length = 64)
    private String deviceClass;

    @Column(length = 128)
    private String osName;

    @Column(length = 128)
    private String agentName;

    @Column(length = 64)
    private String agentClass;

    @Column(length = 8)
    private String countryIso;

    @Column(length = 128)
    private String countryName;

    @Column(length = 128)
    private String regionName;

    @Column(length = 128)
    private String cityName;

    @Column(nullable = false)
    private Instant clickedAt;

    protected ClickLog() {
    }

    public ClickLog(
            String slug,
            String originalUrl,
            String ipAddress,
            String userAgent,
            String deviceClass,
            String osName,
            String agentName,
            String agentClass,
            String countryIso,
            String countryName,
            String regionName,
            String cityName
    ) {
        this.slug = slug;
        this.originalUrl = originalUrl;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.deviceClass = deviceClass;
        this.osName = osName;
        this.agentName = agentName;
        this.agentClass = agentClass;
        this.countryIso = countryIso;
        this.countryName = countryName;
        this.regionName = regionName;
        this.cityName = cityName;
        this.clickedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getDeviceClass() {
        return deviceClass;
    }

    public String getOsName() {
        return osName;
    }

    public String getAgentName() {
        return agentName;
    }

    public String getAgentClass() {
        return agentClass;
    }

    public String getCountryIso() {
        return countryIso;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getCityName() {
        return cityName;
    }

    public Instant getClickedAt() {
        return clickedAt;
    }
}
