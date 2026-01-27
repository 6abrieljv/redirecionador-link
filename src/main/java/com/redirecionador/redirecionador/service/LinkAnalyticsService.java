package com.redirecionador.redirecionador.service;

import com.redirecionador.redirecionador.dto.LinkAnalyticsResponse;
import com.redirecionador.redirecionador.exception.SlugNotFoundException;
import com.redirecionador.redirecionador.model.ShortLink;
import com.redirecionador.redirecionador.repository.ClickLogRepository;
import com.redirecionador.redirecionador.repository.ShortLinkRepository;
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

        return new LinkAnalyticsResponse(
                slug,
                shortLink.getOriginalUrl(),
                totalClicks,
                clickLogRepository.countByDeviceClass(slug),
                clickLogRepository.countByOsName(slug),
                clickLogRepository.countByAgentName(slug),
                clickLogRepository.countByCountryName(slug)
        );
    }
}
