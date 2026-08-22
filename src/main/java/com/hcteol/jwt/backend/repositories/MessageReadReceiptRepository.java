package com.hcteol.jwt.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.MessageReadReceipt;

@Repository
public interface MessageReadReceiptRepository extends JpaRepository<MessageReadReceipt, Long> {

    List<MessageReadReceipt> findByMessageIdIn(List<Long> messageIds);

    List<MessageReadReceipt> findByStaffId(String staffId);

    Optional<MessageReadReceipt> findByMessageIdAndStaffId(Long messageId, String staffId);
}
