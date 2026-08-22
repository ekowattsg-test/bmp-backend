package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRecipientTypeAndRecipientStaffIdOrderByCreatedAtDesc(String recipientType, String recipientStaffId);

    List<Message> findByRecipientTypeAndProjectCodeOrderByCreatedAtDesc(String recipientType, String projectCode);

    List<Message> findByRecipientTypeOrderByCreatedAtDesc(String recipientType);

    List<Message> findBySenderStaffIdAndRecipientTypeAndRecipientStaffIdOrderByCreatedAtDesc(
            String senderStaffId, String recipientType, String recipientStaffId);
}
