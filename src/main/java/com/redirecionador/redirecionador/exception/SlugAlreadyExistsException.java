package com.redirecionador.redirecionador.exception;

public class SlugAlreadyExistsException extends RuntimeException {
    private final String slug;

    public SlugAlreadyExistsException(String slug) {
        super("Slug already exists: " + slug);
        this.slug = slug;
    }

    public String getSlug() {
        return slug;
    }
}
