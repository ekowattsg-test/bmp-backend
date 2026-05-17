package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.DocumentSeq;
import com.hcteol.jwt.backend.services.DocumentSeqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hcteol.jwt.backend.dtos.DocumentSeqRequest;

import java.util.List;

@RestController
@RequestMapping("/api/documentseqs")
public class DocumentSeqController {

    @Autowired
    private DocumentSeqService documentSeqService;

    @GetMapping
    public List<DocumentSeq> getAllDocumentSeqs() {
        return documentSeqService.getAllDocumentSeqs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentSeq> getDocumentSeqById(@PathVariable String id) {
        return documentSeqService.getDocumentSeqById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public DocumentSeq createDocumentSeq(@RequestBody DocumentSeq ds) {
        return documentSeqService.addDocumentSeq(ds);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentSeq> updateDocumentSeq(@PathVariable String id, @RequestBody DocumentSeq ds) {
        DocumentSeq updated = documentSeqService.updateDocumentSeq(id, ds);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumentSeq(@PathVariable String id) {
        documentSeqService.deleteDocumentSeq(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/getSeq")
    public ResponseEntity<Long> getSeq(@RequestBody DocumentSeqRequest req) {
        if (req == null || req.getDocType() == null) {
            return ResponseEntity.badRequest().build();
        }
        Long seq = documentSeqService.getNextSeq(req.getDocType(), req.getToken());
        return ResponseEntity.ok(seq);
    }
}
