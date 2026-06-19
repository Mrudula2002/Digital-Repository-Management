package com.drp.repository;

import com.drp.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    @Query("""
            SELECT r FROM Resource r
            WHERE (:keyword IS NULL OR :keyword = '' OR
                   LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(r.tags) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:categoryId IS NULL OR r.category.id = :categoryId)
            AND (:fileType IS NULL OR :fileType = '' OR r.fileType = :fileType)
            """)
    Page<Resource> search(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("fileType") String fileType,
            Pageable pageable
    );
}
