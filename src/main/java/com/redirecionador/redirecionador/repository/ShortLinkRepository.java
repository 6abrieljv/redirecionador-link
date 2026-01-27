package com.redirecionador.redirecionador.repository;

import com.redirecionador.redirecionador.model.ShortLink;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortLinkRepository extends JpaRepository<ShortLink, Long> {
    Optional<ShortLink> findBySlug(String slug);
}
