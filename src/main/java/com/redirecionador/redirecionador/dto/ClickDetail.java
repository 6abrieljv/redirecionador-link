package com.redirecionador.redirecionador.dto;

import java.time.Instant;

public record ClickDetail(
        String ipAddress,
        String countryName,
        String regionName,
        String cityName,
        String deviceClass,
        String osName,
        String agentName,
        Instant clickedAt
) {
}
