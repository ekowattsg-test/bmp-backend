package com.hcteol.jwt.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hcteol.jwt.backend.entities.OperationStaff;
import java.util.List;

public interface OperationStaffRepository extends JpaRepository<OperationStaff, Long> {

    List<OperationStaff> findByStaffId(String staffId);

    List<OperationStaff> findByRoleName(String roleName);

    void deleteByStaffId(String staffId);
}
