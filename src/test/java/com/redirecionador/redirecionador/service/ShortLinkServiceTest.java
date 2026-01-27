package com.redirecionador.redirecionador.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.redirecionador.redirecionador.exception.InvalidRedirectUrlException;
import com.redirecionador.redirecionador.exception.SlugAlreadyExistsException;
import com.redirecionador.redirecionador.model.ShortLink;
import com.redirecionador.redirecionador.repository.ShortLinkRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ShortLinkServiceTest {

    @Test
    void createWithProvidedSlug() {
        ShortLinkRepository repository = mock(ShortLinkRepository.class);
        ShortLinkService service = new ShortLinkService(repository);

        when(repository.findBySlug("meu-link")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ShortLink result = service.create("meu-link", "https://example.com");

        assertEquals("meu-link", result.getSlug());
        assertEquals("https://example.com", result.getOriginalUrl());
    }

    @Test
    void createGeneratesSlugWhenMissing() {
        ShortLinkRepository repository = mock(ShortLinkRepository.class);
        ShortLinkService service = new ShortLinkService(repository);

        when(repository.findBySlug(any())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ShortLink result = service.create(null, "https://example.com");

        assertNotNull(result.getSlug());
        assertEquals(6, result.getSlug().length());
        assertEquals("https://example.com", result.getOriginalUrl());
    }

    @Test
    void createFailsWhenSlugAlreadyExists() {
        ShortLinkRepository repository = mock(ShortLinkRepository.class);
        ShortLinkService service = new ShortLinkService(repository);

        when(repository.findBySlug(eq("dup"))).thenReturn(Optional.of(mock(ShortLink.class)));

        assertThrows(SlugAlreadyExistsException.class,
                () -> service.create("dup", "https://example.com"));
    }

    @Test
    void createFailsForInvalidUrl() {
        ShortLinkRepository repository = mock(ShortLinkRepository.class);
        ShortLinkService service = new ShortLinkService(repository);

        assertThrows(InvalidRedirectUrlException.class,
                () -> service.create("abc", "ftp://example.com"));
    }
}
