package com.hcteol.jwt.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.Vehicle;
import com.hcteol.jwt.backend.repositories.VehicleRepository;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public Vehicle addVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public java.util.Optional<Vehicle> getVehicleById(String id) {
        return vehicleRepository.findById(id);
    }

    public Vehicle updateVehicle(String id, Vehicle details) {
        var existing = vehicleRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setDriver(details.getDriver());
            existing.setActive(details.getActive());
            return vehicleRepository.save(existing);
        }
        return null;
    }

    public void deleteVehicle(String id) {
        vehicleRepository.deleteById(id);
    }
}
