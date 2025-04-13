package com.java.DocumentIngestion.service;


import com.java.DocumentIngestion.entity.Document;

import com.java.DocumentIngestion.exception.*;
import com.java.DocumentIngestion.repository.DocumentRepository;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final Tika tika = new Tika();

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Document uploadFile(MultipartFile file, String author) throws Exception {
        try {
            if (file == null || file.isEmpty()) {
                throw new InvalidDocumentException("Uploaded file is empty or null.");
            }

            String text = tika.parseToString(file.getInputStream());

            Document doc = new Document();
            doc.setTitle(file.getOriginalFilename());
            doc.setAuthor(author);
            doc.setType(file.getContentType());
            doc.setFileName(file.getOriginalFilename());
            doc.setUploadDate(LocalDateTime.now());
            doc.setContent(text);

            return documentRepository.save(doc);

        } catch (IOException e) {
            throw new DocumentProcessingException("Failed to read the file content.", e);
        } catch (TikaException e) {
            throw new DocumentProcessingException("Failed to parse the file content.", e);
        } catch (Exception e) {
            throw new DocumentProcessingException("Unexpected error during file upload.", e);
        }
    }

    public List<Document> searchByKeyword(String keyword) {
        List<Document> results = documentRepository.searchByKeyword(keyword);
        if (results.isEmpty()) {
            throw new DocumentKeyNotFoundException("Search key not found in the file : " + keyword);
        }
        return results;
    }

    public List<Document> filterByAuthor(String author) {
        List<Document> docs = documentRepository.findByAuthorContainingIgnoreCase(author);
        if (docs == null ||docs.isEmpty() || author == null || author.trim().isEmpty()) {
            throw new DocumentNotFoundException("Search author not found in the file : " + author);
        }
        return docs;
      }


    public Page<Document> filterByAuthors(String author, Pageable pageable) {
        if (author == null || author.trim().isEmpty()) {
            throw new DocumentNotFoundException("Author name must not be null or empty : "+author);
        }
        return documentRepository.findByAuthorContainingIgnoreCase(author, pageable);
    }

    public Page<Document> findAll(Pageable pageable) {
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new InvalidPaginationException("Page number must be ≥ 0 and size must be > 0");
        }

        try {
            return documentRepository.findAll(pageable);
        } catch (Exception e) {
            throw new DatabaseAccessException("Unable to retrieve documents. Please try again later.");
        }

    }

    public Page<Document> search(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            throw new InvalidSearchQueryException("Search query must not be empty");
        }

        try {
            return documentRepository.searchByContent(query, pageable);
        } catch (Exception ex) {
            throw new DatabaseAccessException("Failed to perform search. Please try again later.");
        }
    }

    public Document  getById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));
    }
}



