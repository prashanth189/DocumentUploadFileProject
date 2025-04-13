package com.java.DocumentIngestion.repository;

import com.java.DocumentIngestion.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByAuthorContainingIgnoreCase(String author);

    @Query("SELECT d FROM Document d WHERE d.content LIKE %:keyword%")
    List<Document> searchByKeyword(@Param("keyword") String keyword);

    // Filter by metadata
    Page<Document> findByAuthorContainingIgnoreCase(String author, Pageable pageable);

    // Full-text search using ILIKE (for PostgreSQL), or LIKE for MySQL
    @Query("SELECT d FROM Document d WHERE LOWER(d.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Document> searchByContent(String query, Pageable pageable);


}