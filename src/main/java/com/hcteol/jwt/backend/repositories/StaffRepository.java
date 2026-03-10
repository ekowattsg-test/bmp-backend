package com.hcteol.jwt.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcteol.jwt.backend.entities.Staff;

public interface StaffRepository extends JpaRepository<Staff, String> {

}
