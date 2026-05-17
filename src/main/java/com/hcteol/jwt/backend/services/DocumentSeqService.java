package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.DocumentSeq;
import com.hcteol.jwt.backend.repositories.DocumentSeqRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentSeqService {

    @Autowired
    private DocumentSeqRepository documentSeqRepository;

    public DocumentSeq addDocumentSeq(DocumentSeq ds) {
        return documentSeqRepository.save(ds);
    }

    public List<DocumentSeq> getAllDocumentSeqs() {
        return documentSeqRepository.findAll();
    }

    public Optional<DocumentSeq> getDocumentSeqById(String id) {
        return documentSeqRepository.findById(id);
    }

    public DocumentSeq updateDocumentSeq(String id, DocumentSeq details) {
        var existing = documentSeqRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setSeq(details.getSeq());
            existing.setToken(details.getToken());
            return documentSeqRepository.save(existing);
        }
        return null;
    }

    public void deleteDocumentSeq(String id) {
        documentSeqRepository.deleteById(id);
    }

    @Transactional
    public Long getNextSeq(String docType, String token) {
        java.util.Optional<DocumentSeq> opt = documentSeqRepository.findByDocTypeForUpdate(docType);
        DocumentSeq ds;
        if (opt.isPresent()) {
            ds = opt.get();
            Long current = ds.getSeq() == null ? 0L : ds.getSeq();
            ds.setSeq(current + 1);
            ds.setToken(token);
        } else {
            ds = new DocumentSeq();
            ds.setDocType(docType);
            ds.setSeq(1L);
            ds.setToken(token);
        }
        documentSeqRepository.save(ds);
        return ds.getSeq();
    }
}
