package com.hcteol.jwt.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.Staff;
import com.hcteol.jwt.backend.repositories.StaffRepository;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    public Staff addStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public Staff getStaffByName(String staffName) {
        return staffRepository.findById(staffName).orElse(null);
    }

    public Staff updateStaff(String staffName, Staff staffDetails) {
        Staff existingStaff = staffRepository.findById(staffName).orElse(null);
        if (existingStaff != null) {
            existingStaff.setStaffId(staffDetails.getStaffId());
            existingStaff.setMobileNumber(staffDetails.getMobileNumber());
            existingStaff.setStaffRoleCode(staffDetails.getStaffRoleCode());
            existingStaff.setServiceStartDate(staffDetails.getServiceStartDate());
            existingStaff.setServiceEndDate(staffDetails.getServiceEndDate());
            existingStaff.setDepartment(staffDetails.getDepartment());
            existingStaff.setStaffNumber(staffDetails.getStaffNumber());
            existingStaff.setCompanyId(staffDetails.getCompanyId());
            existingStaff.setActive(staffDetails.getActive());
            return staffRepository.save(existingStaff);
        }
        return null;
    }

    public void deleteStaff(String staffName) {
        staffRepository.deleteById(staffName);
    }
}
