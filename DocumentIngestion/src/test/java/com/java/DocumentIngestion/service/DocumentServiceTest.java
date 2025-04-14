package com.java.DocumentIngestion.service;

import com.java.DocumentIngestion.entity.Document;
import com.java.DocumentIngestion.exception.*;
import com.java.DocumentIngestion.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private Pageable pageable;

    @InjectMocks
    private DocumentService documentService;


    // --- uploadFile Tests ---

    @Test
    void uploadFile_validFile_shouldReturnSavedDocument() throws Exception {
        // Arrange
        String content = "Hello world!";
        MultipartFile file = new MockMultipartFile("file", "hello.txt", "text/plain", content.getBytes());

        Document doc = new Document();
        doc.setId(1L);
        doc.setTitle("hello.txt");
        doc.setAuthor("John");
        doc.setFileName("hello.txt");
        doc.setType("text/plain");
        doc.setContent(content);

        when(documentRepository.save(any(Document.class))).thenReturn(doc);

        // Act
        Document result = documentService.uploadFile(file, "John");

        // Assert
        assertNotNull(result);
        assertEquals("hello.txt", result.getTitle());
        assertEquals("John", result.getAuthor());
    }



    @Test
    void uploadFile_nullFile_shouldThrowException() {
        // Act & Assert
        DocumentProcessingException exception = assertThrows(DocumentProcessingException.class, () -> {
            documentService.uploadFile(null, "Author");
        });

        // Assert that the cause of the DocumentProcessingException is an InvalidDocumentException
        assertTrue(exception.getCause() instanceof InvalidDocumentException);

        // Optional: Assert the message of the InvalidDocumentException if desired
        assertEquals("Uploaded file is empty or null.", exception.getCause().getMessage());
    }

    // --- filterByAuthor Tests ---

    @Test
    void filterByAuthor_valid_shouldReturnDocs() {
        List<Document> docs = List.of(new Document());
        when(documentRepository.findByAuthorContainingIgnoreCase("Alice")).thenReturn(docs);

        List<Document> result = documentService.filterByAuthor("Alice");

        assertFalse(result.isEmpty());
    }

    @Test
    void filterByAuthor_invalid_shouldThrowException() {
        when(documentRepository.findByAuthorContainingIgnoreCase("")).thenReturn(Collections.emptyList());

        assertThrows(DocumentNotFoundException.class, () -> {
            documentService.filterByAuthor("");
        });
    }



    // --- searchByKeyword Tests ---

    @Test
    void searchByKeyword_found_shouldReturnList() {
        List<Document> docs = List.of(new Document());
        when(documentRepository.searchByKeyword("test")).thenReturn(docs);

        List<Document> result = documentService.searchByKeyword("test");

        assertEquals(1, result.size());
    }

    @Test
    void searchByKeyword_notFound_shouldThrowException() {
        when(documentRepository.searchByKeyword("missing")).thenReturn(Collections.emptyList());

        assertThrows(DocumentKeyNotFoundException.class, () -> {
            documentService.searchByKeyword("missing");
        });
    }

    // --- getById Tests ---

    @Test
    void getById_found_shouldReturnDocument() {
        Document doc = new Document();
        doc.setId(1L);
        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));

        Document result = documentService.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getById_notFound_shouldThrowException() {
        when(documentRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            documentService.getById(2L);
        });
    }

    @Test
    void search_validQuery_shouldReturnPageOfDocuments() {
        // Arrange
        String query = "valid query";
        Document document = new Document();
        document.setId(1L);
        document.setContent("Some content");
        Page<Document> page = new PageImpl<>(Collections.singletonList(document));

        when(documentRepository.searchByContent(query, pageable)).thenReturn(page);

        // Act
        Page<Document> result = documentService.search(query, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Some content", result.getContent().get(0).getContent());
    }

    @Test
    void search_emptyQuery_shouldThrowInvalidSearchQueryException() {
        // Arrange
        String query = "";

        // Act & Assert
        InvalidSearchQueryException exception = assertThrows(InvalidSearchQueryException.class, () -> {
            documentService.search(query, pageable);
        });

        // Assert
        assertEquals("Search query must not be empty", exception.getMessage());
    }

    @Test
    void search_nullQuery_shouldThrowInvalidSearchQueryException() {
        // Arrange
        String query = null;

        // Act & Assert
        InvalidSearchQueryException exception = assertThrows(InvalidSearchQueryException.class, () -> {
            documentService.search(query, pageable);
        });

        // Assert
        assertEquals("Search query must not be empty", exception.getMessage());
    }

    @Test
    void search_repositoryException_shouldThrowDatabaseAccessException() {
        // Arrange
        String query = "valid query";
        when(documentRepository.searchByContent(query, pageable)).thenThrow(new RuntimeException("Database issue"));

        // Act & Assert
        DatabaseAccessException exception = assertThrows(DatabaseAccessException.class, () -> {
            documentService.search(query, pageable);
        });

        // Assert
        assertEquals("Failed to perform search. Please try again later.", exception.getMessage());
    }

}