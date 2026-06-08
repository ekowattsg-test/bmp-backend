package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.entities.Staff;
import com.hcteol.jwt.backend.repositories.StaffRepository;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private DocumentSeqService documentSeqService;

    @Transactional
    public Staff addStaff(Staff staff) {
        if (staff.getStaffId() == null || staff.getStaffId().trim().isEmpty()) {
            String token = UUID.randomUUID().toString();
            Long seq = documentSeqService.getNextSeq("ST", token);
            staff.setStaffId("ST-" + seq);
        }
        return staffRepository.save(staff);
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public java.util.Optional<Staff> getStaffById(String staffId) {
        return staffRepository.findById(staffId);
    }

    public java.util.Optional<Staff> getStaffByMobileNumber(String mobileNumber) {
        return staffRepository.findByMobileNumber(mobileNumber);
    }

    public Staff updateStaff(String staffId, Staff staffDetails) {
        Staff existingStaff = staffRepository.findById(staffId).orElse(null);
        if (existingStaff != null) {
            // do not overwrite primary key
            existingStaff.setMobileNumber(staffDetails.getMobileNumber());
            existingStaff.setStaffName(staffDetails.getStaffName());
            existingStaff.setStaffRoleCode(staffDetails.getStaffRoleCode());
            existingStaff.setServiceStartDate(staffDetails.getServiceStartDate());
            existingStaff.setServiceEndDate(staffDetails.getServiceEndDate());
            existingStaff.setDepartment(staffDetails.getDepartment());
            existingStaff.setStaffNumber(staffDetails.getStaffNumber());
            existingStaff.setStaffType(staffDetails.getStaffType());
            existingStaff.setLocation(staffDetails.getLocation());
            existingStaff.setCompanyId(staffDetails.getCompanyId());
            existingStaff.setActive(staffDetails.getActive());
            return staffRepository.save(existingStaff);
        }
        return null;
    }

    public void deleteStaff(String staffId) {
        staffRepository.deleteById(staffId);
    }
}
