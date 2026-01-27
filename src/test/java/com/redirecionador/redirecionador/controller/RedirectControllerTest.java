package com.redirecionador.redirecionador.controller;

import com.redirecionador.redirecionador.exception.ApiExceptionHandler;
import com.redirecionador.redirecionador.exception.InvalidRedirectUrlException;
import com.redirecionador.redirecionador.exception.SlugNotFoundException;
import com.redirecionador.redirecionador.service.RedirectService;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RedirectControllerTest {

    @Test
    void redirectReturnsFound() {
        RedirectService redirectService = mock(RedirectService.class);
        RedirectController controller = new RedirectController(redirectService);
        MockHttpServletRequest request = new MockHttpServletRequest();

        when(redirectService.resolveRedirect(eq("abc123"), any()))
                .thenReturn(URI.create("https://example.com"));

        ResponseEntity<Void> response = controller.redirect("abc123", request);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(URI.create("https://example.com"), response.getHeaders().getLocation());
    }

    @Test
    void handlerReturnsNotFoundForMissingSlug() {
        ApiExceptionHandler handler = new ApiExceptionHandler();

        ResponseEntity<ApiExceptionHandler.ErrorResponse> response =
                handler.handleSlugNotFound(new SlugNotFoundException("missing"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("slug_not_found", response.getBody().error());
        assertEquals("Slug not found: missing", response.getBody().message());
    }

    @Test
    void handlerReturnsBadRequestForInvalidUrl() {
        ApiExceptionHandler handler = new ApiExceptionHandler();

        ResponseEntity<ApiExceptionHandler.ErrorResponse> response =
                handler.handleInvalidRedirectUrl(new InvalidRedirectUrlException("ftp://example.com"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("invalid_redirect_url", response.getBody().error());
        assertEquals("Invalid redirect URL: ftp://example.com", response.getBody().message());
    }
}
