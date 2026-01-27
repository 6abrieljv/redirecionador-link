package com.redirecionador.redirecionador.service;

import com.redirecionador.redirecionador.exception.InvalidRedirectUrlException;
import com.redirecionador.redirecionador.exception.SlugAlreadyExistsException;
import com.redirecionador.redirecionador.model.ShortLink;
import com.redirecionador.redirecionador.repository.ShortLinkRepository;
import java.security.SecureRandom;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ShortLinkService {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int DEFAULT_SLUG_LENGTH = 6;
    private static final int MAX_GENERATION_ATTEMPTS = 5;

    private final ShortLinkRepository shortLinkRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public ShortLinkService(ShortLinkRepository shortLinkRepository) {
        this.shortLinkRepository = shortLinkRepository;
    }

    public ShortLink create(String slug, String originalUrl) {
        if (!hasHttpScheme(originalUrl)) {
            throw new InvalidRedirectUrlException(originalUrl);
        }

        String normalizedSlug = normalizeSlug(slug);
        if (normalizedSlug == null) {
            normalizedSlug = generateUniqueSlug(DEFAULT_SLUG_LENGTH);
        } else if (shortLinkRepository.findBySlug(normalizedSlug).isPresent()) {
            throw new SlugAlreadyExistsException(normalizedSlug);
        }

        ShortLink shortLink = new ShortLink(normalizedSlug, originalUrl);
        return shortLinkRepository.save(shortLink);
    }

    private String normalizeSlug(String slug) {
        if (slug == null) {
            return null;
        }
        String trimmed = slug.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed;
    }

    private String generateUniqueSlug(int length) {
        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            String candidate = generateSlug(length);
            Optional<ShortLink> existing = shortLinkRepository.findBySlug(candidate);
            if (existing.isEmpty()) {
                return candidate;
            }
        }
        throw new SlugAlreadyExistsException("auto-generated");
    }

    private String generateSlug(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(ALPHABET.length());
            builder.append(ALPHABET.charAt(index));
        }
        return builder.toString();
    }

    private boolean hasHttpScheme(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }
}
