package com.redirecionador.redirecionador.repository;

import com.redirecionador.redirecionador.dto.LabelCount;
import com.redirecionador.redirecionador.model.ClickLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClickLogRepository extends JpaRepository<ClickLog, Long> {
    long countBySlug(String slug);

    @Query("""
            select new com.redirecionador.redirecionador.dto.LabelCount(
                coalesce(cl.deviceClass, 'Unknown'),
                count(cl)
            )
            from ClickLog cl
            where cl.slug = :slug
            group by coalesce(cl.deviceClass, 'Unknown')
            order by count(cl) desc
            """)
    List<LabelCount> countByDeviceClass(@Param("slug") String slug);

    @Query("""
            select new com.redirecionador.redirecionador.dto.LabelCount(
                coalesce(cl.osName, 'Unknown'),
                count(cl)
            )
            from ClickLog cl
            where cl.slug = :slug
            group by coalesce(cl.osName, 'Unknown')
            order by count(cl) desc
            """)
    List<LabelCount> countByOsName(@Param("slug") String slug);

    @Query("""
            select new com.redirecionador.redirecionador.dto.LabelCount(
                coalesce(cl.agentName, 'Unknown'),
                count(cl)
            )
            from ClickLog cl
            where cl.slug = :slug
            group by coalesce(cl.agentName, 'Unknown')
            order by count(cl) desc
            """)
    List<LabelCount> countByAgentName(@Param("slug") String slug);

    @Query("""
            select new com.redirecionador.redirecionador.dto.LabelCount(
                coalesce(cl.countryName, 'Unknown'),
                count(cl)
            )
            from ClickLog cl
            where cl.slug = :slug
            group by coalesce(cl.countryName, 'Unknown')
            order by count(cl) desc
            """)
    List<LabelCount> countByCountryName(@Param("slug") String slug);

    List<ClickLog> findTop20BySlugOrderByClickedAtDesc(String slug);
}
