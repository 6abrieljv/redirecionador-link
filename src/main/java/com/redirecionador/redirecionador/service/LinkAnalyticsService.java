package com.redirecionador.redirecionador.service;

import com.redirecionador.redirecionador.dto.ClickDetail;
import com.redirecionador.redirecionador.dto.LinkAnalyticsResponse;
import com.redirecionador.redirecionador.exception.SlugNotFoundException;
import com.redirecionador.redirecionador.model.ClickLog;
import com.redirecionador.redirecionador.model.ShortLink;
import com.redirecionador.redirecionador.repository.ClickLogRepository;
import com.redirecionador.redirecionador.repository.ShortLinkRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LinkAnalyticsService {
    private final ClickLogRepository clickLogRepository;
    private final ShortLinkRepository shortLinkRepository;

    public LinkAnalyticsService(
            ClickLogRepository clickLogRepository,
            ShortLinkRepository shortLinkRepository
    ) {
        this.clickLogRepository = clickLogRepository;
        this.shortLinkRepository = shortLinkRepository;
    }

    public LinkAnalyticsResponse getAnalytics(String slug) {
        ShortLink shortLink = shortLinkRepository.findBySlug(slug)
                .orElseThrow(() -> new SlugNotFoundException(slug));

        long totalClicks = clickLogRepository.countBySlug(slug);
        List<ClickDetail> recentClicks = clickLogRepository.findTop20BySlugOrderByClickedAtDesc(slug)
                .stream()
                .map(this::toClickDetail)
                .toList();

        return new LinkAnalyticsResponse(
                slug,
                shortLink.getOriginalUrl(),
                totalClicks,
                clickLogRepository.countByDeviceClass(slug),
                clickLogRepository.countByOsName(slug),
                clickLogRepository.countByAgentName(slug),
                clickLogRepository.countByCountryName(slug),
                recentClicks
        );
    }

    private ClickDetail toClickDetail(ClickLog clickLog) {
        return new ClickDetail(
                valueOrUnknown(clickLog.getIpAddress()),
                valueOrUnknown(clickLog.getCountryName()),
                valueOrUnknown(clickLog.getRegionName()),
                valueOrUnknown(clickLog.getCityName()),
                valueOrUnknown(clickLog.getDeviceClass()),
                valueOrUnknown(clickLog.getOsName()),
                valueOrUnknown(clickLog.getAgentName()),
                clickLog.getClickedAt()
        );
    }

    private String valueOrUnknown(String value) {
        if (value == null || value.isBlank()) {
            return "Unknown";
        }
        return value;
    }
}
