package com.redirecionador.redirecionador.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.redirecionador.redirecionador.exception.InvalidRedirectUrlException;
import com.redirecionador.redirecionador.model.ClickLog;
import com.redirecionador.redirecionador.repository.ClickLogRepository;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;

class RedirectServiceTest {

    @Test
    void resolveRedirectBuildsLocationAndSavesClickLog() {
        ShortLinkLookupService shortLinkLookupService = mock(ShortLinkLookupService.class);
        ClickLogRepository clickLogRepository = mock(ClickLogRepository.class);
        UserAgentService userAgentService = mock(UserAgentService.class);
        GeoIpService geoIpService = mock(GeoIpService.class);

        RedirectService redirectService = new RedirectService(
                shortLinkLookupService,
                clickLogRepository,
                userAgentService,
                geoIpService
        );

        when(shortLinkLookupService.getOriginalUrl("abc"))
                .thenReturn("https://example.com");
        when(userAgentService.parse("Mozilla"))
                .thenReturn(new UserAgentService.UserAgentDetails("Desktop", "Windows", "Chrome", "Browser"));
        when(geoIpService.resolve("203.0.113.10"))
                .thenReturn(new GeoIpService.GeoIpDetails("BR", "Brazil", "SP", "Sao Paulo"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "203.0.113.10, 70.0.0.1");
        request.addHeader("User-Agent", "Mozilla");
        request.setRemoteAddr("10.0.0.1");

        URI redirectUri = redirectService.resolveRedirect("abc", request);

        assertEquals(URI.create("https://example.com"), redirectUri);
        verify(geoIpService).resolve(eq("203.0.113.10"));

        ArgumentCaptor<ClickLog> captor = ArgumentCaptor.forClass(ClickLog.class);
        verify(clickLogRepository).save(captor.capture());

        ClickLog log = captor.getValue();
        assertEquals("abc", log.getSlug());
        assertEquals("https://example.com", log.getOriginalUrl());
        assertEquals("203.0.113.10", log.getIpAddress());
        assertEquals("Mozilla", log.getUserAgent());
        assertEquals("Desktop", log.getDeviceClass());
        assertEquals("Windows", log.getOsName());
        assertEquals("Chrome", log.getAgentName());
        assertEquals("Browser", log.getAgentClass());
        assertEquals("BR", log.getCountryIso());
        assertEquals("Brazil", log.getCountryName());
        assertEquals("SP", log.getRegionName());
        assertEquals("Sao Paulo", log.getCityName());
        assertNotNull(log.getClickedAt());
    }

    @Test
    void resolveRedirectRejectsInvalidUrl() {
        ShortLinkLookupService shortLinkLookupService = mock(ShortLinkLookupService.class);
        ClickLogRepository clickLogRepository = mock(ClickLogRepository.class);
        UserAgentService userAgentService = mock(UserAgentService.class);
        GeoIpService geoIpService = mock(GeoIpService.class);

        RedirectService redirectService = new RedirectService(
                shortLinkLookupService,
                clickLogRepository,
                userAgentService,
                geoIpService
        );

        when(shortLinkLookupService.getOriginalUrl("bad"))
                .thenReturn("ftp://example.com");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.1");

        assertThrows(InvalidRedirectUrlException.class,
                () -> redirectService.resolveRedirect("bad", request));

        verifyNoInteractions(clickLogRepository);
    }
}
