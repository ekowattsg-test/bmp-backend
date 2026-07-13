package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class LibraryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long libraryEntryId;
    private Long libraryCatelogId;
    private String libraryEntryName;
    private String libraryEntryType; // link = url, doc = doc in filebrowser
    @Column(columnDefinition = "TEXT")
    private String libraryEntryKey;
    @Column(columnDefinition = "TEXT")
    private String entryQuickSearchKey;
}
