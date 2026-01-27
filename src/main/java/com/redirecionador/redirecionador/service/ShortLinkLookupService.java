package com.redirecionador.redirecionador.service;

import com.redirecionador.redirecionador.exception.SlugNotFoundException;
import com.redirecionador.redirecionador.model.ShortLink;
import com.redirecionador.redirecionador.repository.ShortLinkRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ShortLinkLookupService {
    private final ShortLinkRepository shortLinkRepository;

    public ShortLinkLookupService(ShortLinkRepository shortLinkRepository) {
        this.shortLinkRepository = shortLinkRepository;
    }

    @Cacheable(cacheNames = "shortLinkUrl", key = "#slug")
    public String getOriginalUrl(String slug) {
        return shortLinkRepository.findBySlug(slug)
                .map(ShortLink::getOriginalUrl)
                .orElseThrow(() -> new SlugNotFoundException(slug));
    }
}
