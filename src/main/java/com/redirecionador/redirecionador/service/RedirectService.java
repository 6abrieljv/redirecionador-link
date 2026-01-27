package com.redirecionador.redirecionador.service;

import com.redirecionador.redirecionador.exception.InvalidRedirectUrlException;
import com.redirecionador.redirecionador.model.ClickLog;
import com.redirecionador.redirecionador.repository.ClickLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.stereotype.Service;

@Service
public class RedirectService {
    private final ShortLinkLookupService shortLinkLookupService;
    private final ClickLogRepository clickLogRepository;
    private final UserAgentService userAgentService;
    private final GeoIpService geoIpService;

    public RedirectService(
            ShortLinkLookupService shortLinkLookupService,
            ClickLogRepository clickLogRepository,
            UserAgentService userAgentService,
            GeoIpService geoIpService
    ) {
        this.shortLinkLookupService = shortLinkLookupService;
        this.clickLogRepository = clickLogRepository;
        this.userAgentService = userAgentService;
        this.geoIpService = geoIpService;
    }

    public URI resolveRedirect(String slug, HttpServletRequest request) {
        String originalUrl = shortLinkLookupService.getOriginalUrl(slug);
        URI redirectUri = buildRedirectUri(originalUrl);
        saveClickLog(slug, originalUrl, request);
        return redirectUri;
    }

    private URI buildRedirectUri(String originalUrl) {
        if (!hasHttpScheme(originalUrl)) {
            throw new InvalidRedirectUrlException(originalUrl);
        }
        return URI.create(originalUrl);
    }

    private boolean hasHttpScheme(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }

    private void saveClickLog(String slug, String originalUrl, HttpServletRequest request) {
        try {
            String ipAddress = resolveClientIp(request);
            String userAgent = request.getHeader("User-Agent");
            UserAgentService.UserAgentDetails agentDetails = userAgentService.parse(userAgent);
            GeoIpService.GeoIpDetails geoDetails = geoIpService.resolve(ipAddress);

            ClickLog clickLog = new ClickLog(
                    slug,
                    originalUrl,
                    ipAddress,
                    userAgent,
                    agentDetails.deviceClass(),
                    agentDetails.osName(),
                    agentDetails.agentName(),
                    agentDetails.agentClass(),
                    geoDetails.countryIso(),
                    geoDetails.countryName(),
                    geoDetails.regionName(),
                    geoDetails.cityName()
            );
            clickLogRepository.save(clickLog);
        } catch (Exception ex) {
            // Log falhas de tracking não devem quebrar o redirecionamento.
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}
