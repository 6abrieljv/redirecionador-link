package com.redirecionador.redirecionador.controller;

import com.redirecionador.redirecionador.dto.CreateShortLinkRequest;
import com.redirecionador.redirecionador.dto.CreateShortLinkResponse;
import com.redirecionador.redirecionador.dto.LinkAnalyticsResponse;
import com.redirecionador.redirecionador.model.ShortLink;
import com.redirecionador.redirecionador.service.LinkAnalyticsService;
import com.redirecionador.redirecionador.service.ShortLinkService;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShortLinkController {
    private final ShortLinkService shortLinkService;
    private final LinkAnalyticsService linkAnalyticsService;

    public ShortLinkController(
            ShortLinkService shortLinkService,
            LinkAnalyticsService linkAnalyticsService
    ) {
        this.shortLinkService = shortLinkService;
        this.linkAnalyticsService = linkAnalyticsService;
    }

    @PostMapping("/links")
    public ResponseEntity<CreateShortLinkResponse> create(@RequestBody CreateShortLinkRequest request) {
        ShortLink shortLink = shortLinkService.create(request.slug(), request.originalUrl());
        CreateShortLinkResponse response = new CreateShortLinkResponse(
                shortLink.getSlug(),
                shortLink.getOriginalUrl()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/" + shortLink.getSlug()))
                .body(response);
    }

    @GetMapping("/links/{slug}/analytics")
    public ResponseEntity<LinkAnalyticsResponse> analytics(@PathVariable String slug) {
        return ResponseEntity.ok(linkAnalyticsService.getAnalytics(slug));
    }
}
