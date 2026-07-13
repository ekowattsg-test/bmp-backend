package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.LibraryEntry;
import com.hcteol.jwt.backend.services.LibraryEntryService;

@RestController
@RequestMapping("/api/libraryentries")
public class LibraryEntryController {

    @Autowired
    private LibraryEntryService libraryEntryService;

    @GetMapping
    public List<LibraryEntry> getLibraryEntries(
            @RequestParam(required = false) Long libraryCatelogId,
            @RequestParam(required = false) String libraryEntryType) {
        return libraryEntryService.getLibraryEntries(libraryCatelogId, libraryEntryType);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibraryEntry> getLibraryEntryById(@PathVariable Long id) {
        return libraryEntryService.getLibraryEntryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LibraryEntry> createLibraryEntry(@RequestBody LibraryEntry libraryEntry) {
        LibraryEntry created = libraryEntryService.addLibraryEntry(libraryEntry);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LibraryEntry> updateLibraryEntry(@PathVariable Long id,
            @RequestBody LibraryEntry details) {
        LibraryEntry updated = libraryEntryService.updateLibraryEntry(id, details);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibraryEntry(@PathVariable Long id) {
        libraryEntryService.deleteLibraryEntry(id);
        return ResponseEntity.noContent().build();
    }
}
