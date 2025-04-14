    package com.java.DocumentIngestion.controller;

    import com.java.DocumentIngestion.entity.Document;
    import com.java.DocumentIngestion.service.DocumentService;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.domain.Sort;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import java.util.List;

    @RestController
    @RequestMapping("/api/documents")
    public class DocumentController {

        private final DocumentService service;

        public DocumentController(DocumentService service) {
            this.service = service;
        }

        // Ingest document
        @PostMapping("/upload")
        public ResponseEntity<Document> upload(@RequestParam("file") MultipartFile file,
                                               @RequestParam("author") String author) throws Exception {
            return ResponseEntity.ok(service.uploadFile(file, author));
        }
        //filter by author
        @GetMapping("/filter")
        public ResponseEntity<List<Document>> filterByAuthor(@RequestParam("author") String author) {
            return ResponseEntity.ok(service.filterByAuthor(author));
        }


        // Filter documents by metadata
        @GetMapping
        public Page<Document> getDocuments(
                @RequestParam(required = false) String author,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size,
                @RequestParam(defaultValue = "uploadDate,desc") String[] sort) {

            Sort sortOrder = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
            Pageable pageable = PageRequest.of(page, size, sortOrder);

            if (author != null) {
                return (Page<Document>) service.filterByAuthors(author, pageable);
            } else {
                return service.findAll(pageable);
            }
        }

        // Get document by ID
        @GetMapping("/{id}")
        public Document getById(@PathVariable Long id) {
            return service.getById(id);
        }
    }