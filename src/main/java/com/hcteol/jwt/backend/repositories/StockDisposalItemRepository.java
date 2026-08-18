package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.StockDisposalItem;

@Repository
public interface StockDisposalItemRepository extends JpaRepository<StockDisposalItem, Long> {

    List<StockDisposalItem> findByDisposalId(Long disposalId);
}
