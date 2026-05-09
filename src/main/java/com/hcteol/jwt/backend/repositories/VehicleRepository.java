package com.hcteol.jwt.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcteol.jwt.backend.entities.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {

}
