package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.LibraryEntry;

@Repository
public interface LibraryEntryRepository extends JpaRepository<LibraryEntry, Long> {

    List<LibraryEntry> findByLibraryCatelogId(Long libraryCatelogId);

    List<LibraryEntry> findByLibraryEntryType(String libraryEntryType);

    List<LibraryEntry> findByLibraryCatelogIdAndLibraryEntryType(Long libraryCatelogId, String libraryEntryType);
}
