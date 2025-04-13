package com.java.DocumentIngestion.controller;

import com.java.DocumentIngestion.entity.Document;
import com.java.DocumentIngestion.service.DocumentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/qa")
public class QAController {

    private final DocumentService service;

    public QAController(DocumentService service) {
        this.service = service;
    }
    //serch by any keyword
    @GetMapping("/query")
    public ResponseEntity<List<Document>> query(@RequestParam("question") String question) {
        return ResponseEntity.ok(service.searchByKeyword(question));
    }

    // Basic keyword-based Q&A
    @GetMapping("/search")
    public Page<Document> searchDocuments(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return service.search(query, pageable);
    }

}