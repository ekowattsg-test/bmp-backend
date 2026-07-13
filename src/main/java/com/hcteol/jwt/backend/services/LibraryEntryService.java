package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.LibraryEntry;
import com.hcteol.jwt.backend.repositories.LibraryEntryRepository;

@Service
public class LibraryEntryService {

    @Autowired
    private LibraryEntryRepository libraryEntryRepository;

    public LibraryEntry addLibraryEntry(LibraryEntry libraryEntry) {
        return libraryEntryRepository.save(Objects.requireNonNull(libraryEntry, "libraryEntry cannot be null"));
    }

    public List<LibraryEntry> getAllLibraryEntries() {
        return libraryEntryRepository.findAll();
    }

    public List<LibraryEntry> getLibraryEntries(Long libraryCatelogId, String libraryEntryType) {
        if (libraryCatelogId != null && libraryEntryType != null) {
            return libraryEntryRepository.findByLibraryCatelogIdAndLibraryEntryType(libraryCatelogId, libraryEntryType);
        }
        if (libraryCatelogId != null) {
            return libraryEntryRepository.findByLibraryCatelogId(libraryCatelogId);
        }
        if (libraryEntryType != null) {
            return libraryEntryRepository.findByLibraryEntryType(libraryEntryType);
        }
        return libraryEntryRepository.findAll();
    }

    public List<LibraryEntry> getLibraryEntriesByCatelogId(Long libraryCatelogId) {
        return libraryEntryRepository.findByLibraryCatelogId(
                Objects.requireNonNull(libraryCatelogId, "libraryCatelogId cannot be null"));
    }

    public Optional<LibraryEntry> getLibraryEntryById(Long id) {
        return libraryEntryRepository.findById(Objects.requireNonNull(id, "id cannot be null"));
    }

    public LibraryEntry updateLibraryEntry(Long id, LibraryEntry details) {
        LibraryEntry existing = libraryEntryRepository.findById(Objects.requireNonNull(id, "id cannot be null"))
                .orElse(null);
        if (existing == null) {
            return null;
        }

        BeanUtils.copyProperties(Objects.requireNonNull(details, "details cannot be null"), existing, "libraryEntryId");
        return libraryEntryRepository.save(existing);
    }

    public void deleteLibraryEntry(Long id) {
        libraryEntryRepository.deleteById(Objects.requireNonNull(id, "id cannot be null"));
    }
}
