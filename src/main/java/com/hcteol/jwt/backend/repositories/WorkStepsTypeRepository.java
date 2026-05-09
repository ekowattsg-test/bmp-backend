package com.hcteol.jwt.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hcteol.jwt.backend.entities.WorkStepsType;

public interface WorkStepsTypeRepository extends JpaRepository<WorkStepsType, Long> {

	java.util.Optional<WorkStepsType> findByWorkOrderTypeAndStepNumber(String workOrderType, Integer stepNumber);

}
