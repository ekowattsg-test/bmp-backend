package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.LibraryCatelog;
import com.hcteol.jwt.backend.repositories.LibraryCatelogRepository;

@Service
public class LibraryCatelogService {

    @Autowired
    private LibraryCatelogRepository libraryCatelogRepository;

    public LibraryCatelog addLibraryCatelog(LibraryCatelog libraryCatelog) {
        return libraryCatelogRepository.save(Objects.requireNonNull(libraryCatelog, "libraryCatelog cannot be null"));
    }

    public List<LibraryCatelog> getAllLibraryCatelogs() {
        return libraryCatelogRepository.findAll();
    }

    public List<LibraryCatelog> getLibraryCatelogsByProjectCode(String projectCode) {
        return libraryCatelogRepository.findByProjectCode(
                Objects.requireNonNull(projectCode, "projectCode cannot be null"));
    }

    public Optional<LibraryCatelog> getLibraryCatelogById(Long id) {
        return libraryCatelogRepository.findById(Objects.requireNonNull(id, "id cannot be null"));
    }

    public LibraryCatelog updateLibraryCatelog(Long id, LibraryCatelog details) {
        LibraryCatelog existing = libraryCatelogRepository.findById(Objects.requireNonNull(id, "id cannot be null"))
                .orElse(null);
        if (existing == null) {
            return null;
        }

        BeanUtils.copyProperties(Objects.requireNonNull(details, "details cannot be null"), existing, "libraryCatelogId");
        return libraryCatelogRepository.save(existing);
    }

    public void deleteLibraryCatelog(Long id) {
        libraryCatelogRepository.deleteById(Objects.requireNonNull(id, "id cannot be null"));
    }
}
