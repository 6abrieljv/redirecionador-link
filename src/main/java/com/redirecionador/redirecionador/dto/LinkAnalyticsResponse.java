package com.redirecionador.redirecionador.dto;

import java.util.List;

public record LinkAnalyticsResponse(
        String slug,
        String originalUrl,
        long totalClicks,
        List<LabelCount> deviceClasses,
        List<LabelCount> osNames,
        List<LabelCount> agentNames,
        List<LabelCount> countryNames,
        List<ClickDetail> recentClicks
) {
}
