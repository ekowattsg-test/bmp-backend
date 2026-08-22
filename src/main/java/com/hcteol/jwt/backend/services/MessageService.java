package com.hcteol.jwt.backend.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.dtos.MessageDto;
import com.hcteol.jwt.backend.dtos.SendMessageRequest;
import com.hcteol.jwt.backend.entities.Message;
import com.hcteol.jwt.backend.entities.MessageReadReceipt;
import com.hcteol.jwt.backend.entities.Param;
import com.hcteol.jwt.backend.entities.Staff;
import com.hcteol.jwt.backend.repositories.MessageReadReceiptRepository;
import com.hcteol.jwt.backend.repositories.MessageRepository;
import com.hcteol.jwt.backend.repositories.ParamRepository;
import com.hcteol.jwt.backend.repositories.ProjectStaffRepository;
import com.hcteol.jwt.backend.repositories.StaffRepository;

@Service
public class MessageService {

    private static final Logger LOG = LoggerFactory.getLogger(MessageService.class);

    private static final String TYPE_DIRECT = "DIRECT";
    private static final String TYPE_PROJECT = "PROJECT";
    private static final String TYPE_BROADCAST = "BROADCAST";
    private static final String SCOPE_LEADERSHIP = "LEADERSHIP";
    private static final String SCOPE_ALL = "ALL";
    private static final String SOURCE_SYSTEM = "SYSTEM";
    private static final String SOURCE_USER = "USER";

    private static final String PARAM_CHAT_DEFAULT_SCOPE = "chatProjectGroupDefaultScope";
    private static final String PARAM_CHAT_ALLOW_SCOPE_CHOICE = "chatAllowProjectGroupScopeChoice";

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageReadReceiptRepository readReceiptRepository;

    @Autowired
    private ProjectStaffRepository projectStaffRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private ParamRepository paramRepository;

    @Transactional
    public MessageDto sendMessage(SendMessageRequest request, String senderStaffId) {
        Objects.requireNonNull(request, "request cannot be null");
        if (senderStaffId == null || senderStaffId.isBlank()) {
            throw new IllegalArgumentException("Sender staff id is required");
        }
        String type = normalize(request.getRecipientType());
        if (type == null) {
            throw new IllegalArgumentException("Recipient type is required");
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("Message content is required");
        }

        Message message = new Message();
        message.setSenderStaffId(senderStaffId);
        message.setRecipientType(type);
        message.setContent(request.getContent().trim());
        message.setSource(SOURCE_USER);
        message.setCategory(normalize(request.getCategory()));
        message.setReferenceId(request.getReferenceId());
        message.setCreatedAt(LocalDateTime.now());

        switch (type) {
            case TYPE_DIRECT -> {
                if (request.getRecipientStaffId() == null || request.getRecipientStaffId().isBlank()) {
                    throw new IllegalArgumentException("Recipient staff id is required for direct messages");
                }
                message.setRecipientStaffId(request.getRecipientStaffId().trim());
            }
            case TYPE_PROJECT -> {
                if (request.getProjectCode() == null || request.getProjectCode().isBlank()) {
                    throw new IllegalArgumentException("Project code is required for project messages");
                }
                message.setProjectCode(request.getProjectCode().trim());
                String scope = resolveProjectGroupScope(request.getProjectGroupScope());
                message.setProjectGroupScope(scope);
            }
            case TYPE_BROADCAST -> {
                // broadcast needs no extra target
            }
            default ->
                throw new IllegalArgumentException("Unsupported recipient type: " + type);
        }

        Message saved = messageRepository.save(message);
        return toDto(saved, senderStaffId);
    }

    @Transactional
    public MessageDto sendSystemMessage(SendMessageRequest request) {
        Objects.requireNonNull(request, "request cannot be null");
        String type = normalize(request.getRecipientType());
        if (type == null) {
            throw new IllegalArgumentException("Recipient type is required");
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("Message content is required");
        }

        Message message = new Message();
        message.setSenderStaffId(SOURCE_SYSTEM);
        message.setRecipientType(type);
        message.setContent(request.getContent().trim());
        message.setSource(SOURCE_SYSTEM);
        message.setCategory(normalize(request.getCategory()));
        message.setReferenceId(request.getReferenceId());
        message.setCreatedAt(LocalDateTime.now());

        switch (type) {
            case TYPE_DIRECT -> {
                if (request.getRecipientStaffId() == null || request.getRecipientStaffId().isBlank()) {
                    throw new IllegalArgumentException("Recipient staff id is required for direct messages");
                }
                message.setRecipientStaffId(request.getRecipientStaffId().trim());
            }
            case TYPE_PROJECT -> {
                if (request.getProjectCode() == null || request.getProjectCode().isBlank()) {
                    throw new IllegalArgumentException("Project code is required for project messages");
                }
                message.setProjectCode(request.getProjectCode().trim());
                String scope = resolveProjectGroupScope(request.getProjectGroupScope());
                message.setProjectGroupScope(scope);
            }
            case TYPE_BROADCAST -> {
                // broadcast needs no extra target
            }
            default ->
                throw new IllegalArgumentException("Unsupported recipient type: " + type);
        }

        Message saved = messageRepository.save(message);
        return toDto(saved, null);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getDirectConversation(String viewerStaffId, String otherStaffId) {
        if (viewerStaffId == null || otherStaffId == null) {
            return Collections.emptyList();
        }
        List<Message> sent = messageRepository
                .findBySenderStaffIdAndRecipientTypeAndRecipientStaffIdOrderByCreatedAtDesc(
                        viewerStaffId, TYPE_DIRECT, otherStaffId);
        List<Message> received = messageRepository
                .findByRecipientTypeAndRecipientStaffIdOrderByCreatedAtDesc(
                        TYPE_DIRECT, viewerStaffId);
        received = received.stream()
                .filter(m -> otherStaffId.equalsIgnoreCase(m.getSenderStaffId()))
                .toList();

        List<Message> all = new ArrayList<>();
        all.addAll(sent);
        all.addAll(received);
        all.sort(Comparator.comparing(Message::getCreatedAt));
        return toDtos(all, viewerStaffId);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getProjectMessages(String viewerStaffId, String projectCode) {
        if (viewerStaffId == null || projectCode == null) {
            return Collections.emptyList();
        }
        if (!isProjectMember(viewerStaffId, projectCode, SCOPE_ALL)) {
            throw new IllegalArgumentException("Viewer is not a member of this project");
        }
        List<Message> messages = messageRepository.findByRecipientTypeAndProjectCodeOrderByCreatedAtDesc(
                TYPE_PROJECT, projectCode);
        messages.sort(Comparator.comparing(Message::getCreatedAt));
        return toDtos(messages, viewerStaffId);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getBroadcastMessages(String viewerStaffId) {
        if (viewerStaffId == null) {
            return Collections.emptyList();
        }
        List<Message> messages = messageRepository.findByRecipientTypeOrderByCreatedAtDesc(TYPE_BROADCAST);
        messages.sort(Comparator.comparing(Message::getCreatedAt));
        return toDtos(messages, viewerStaffId);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getConversations(String viewerStaffId) {
        if (viewerStaffId == null) {
            return Collections.emptyList();
        }

        List<Message> directReceived = messageRepository
                .findByRecipientTypeAndRecipientStaffIdOrderByCreatedAtDesc(TYPE_DIRECT, viewerStaffId);
        List<Message> directSent = messageRepository.findAll().stream()
                .filter(m -> TYPE_DIRECT.equalsIgnoreCase(m.getRecipientType()))
                .filter(m -> viewerStaffId.equalsIgnoreCase(m.getSenderStaffId()))
                .toList();
        List<Message> projectMessages = messageRepository.findAll().stream()
                .filter(m -> TYPE_PROJECT.equalsIgnoreCase(m.getRecipientType()))
                .filter(m -> isProjectMember(viewerStaffId, m.getProjectCode(), m.getProjectGroupScope()))
                .toList();
        List<Message> broadcasts = messageRepository.findByRecipientTypeOrderByCreatedAtDesc(TYPE_BROADCAST);

        Map<String, MessageDto> latestByConversation = new HashMap<>();

        for (Message m : directReceived) {
            String key = "DIRECT|" + m.getSenderStaffId();
            updateLatest(latestByConversation, key, m, viewerStaffId);
        }
        for (Message m : directSent) {
            String key = "DIRECT|" + m.getRecipientStaffId();
            updateLatest(latestByConversation, key, m, viewerStaffId);
        }
        for (Message m : projectMessages) {
            String key = "PROJECT|" + m.getProjectCode();
            updateLatest(latestByConversation, key, m, viewerStaffId);
        }
        if (!broadcasts.isEmpty()) {
            Message latest = broadcasts.get(0);
            updateLatest(latestByConversation, "BROADCAST", latest, viewerStaffId);
        }

        return latestByConversation.values().stream()
                .sorted(Comparator.comparing(MessageDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String viewerStaffId) {
        if (viewerStaffId == null) {
            return 0L;
        }
        List<Message> allRelevant = new ArrayList<>();
        allRelevant.addAll(messageRepository.findByRecipientTypeAndRecipientStaffIdOrderByCreatedAtDesc(
                TYPE_DIRECT, viewerStaffId));
        allRelevant.addAll(messageRepository.findByRecipientTypeOrderByCreatedAtDesc(TYPE_BROADCAST));
        allRelevant.addAll(messageRepository.findAll().stream()
                .filter(m -> TYPE_PROJECT.equalsIgnoreCase(m.getRecipientType()))
                .filter(m -> isProjectMember(viewerStaffId, m.getProjectCode(), m.getProjectGroupScope()))
                .toList());

        List<Long> messageIds = allRelevant.stream()
                .filter(m -> !viewerStaffId.equalsIgnoreCase(m.getSenderStaffId()))
                .map(Message::getMessageId)
                .distinct()
                .collect(Collectors.toList());

        if (messageIds.isEmpty()) {
            return 0L;
        }

        List<MessageReadReceipt> receipts = readReceiptRepository.findByMessageIdIn(messageIds);
        Set<Long> readMessageIds = receipts.stream()
                .filter(r -> viewerStaffId.equalsIgnoreCase(r.getStaffId()))
                .map(MessageReadReceipt::getMessageId)
                .collect(Collectors.toSet());

        return messageIds.stream().filter(id -> !readMessageIds.contains(id)).count();
    }

    @Transactional
    public void markAsRead(Long messageId, String viewerStaffId) {
        if (messageId == null || viewerStaffId == null) {
            return;
        }
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            return;
        }
        Message message = messageOpt.get();
        if (!isVisibleTo(viewerStaffId, message)) {
            throw new IllegalArgumentException("Message is not visible to viewer");
        }

        Optional<MessageReadReceipt> existing = readReceiptRepository.findByMessageIdAndStaffId(messageId, viewerStaffId);
        if (existing.isEmpty()) {
            MessageReadReceipt receipt = new MessageReadReceipt();
            receipt.setMessageId(messageId);
            receipt.setStaffId(viewerStaffId);
            receipt.setReadAt(LocalDateTime.now());
            readReceiptRepository.save(receipt);
        }
    }

    @Transactional(readOnly = true)
    public boolean isProjectMember(String staffId, String projectCode, String scope) {
        if (staffId == null || projectCode == null) {
            return false;
        }
        Set<String> members;
        if (SCOPE_ALL.equalsIgnoreCase(scope)) {
            members = projectStaffRepository.findAllStaffIdsByProjectCode(projectCode);
        } else {
            members = projectStaffRepository.findLeaderStaffIdsByProjectCode(projectCode);
        }
        return members.stream().anyMatch(staffId::equalsIgnoreCase);
    }

    @Transactional(readOnly = true)
    public boolean canSendBroadcast(String staffId) {
        // Broadcast restricted to system only by default; override via staff role if needed
        return false;
    }

    @Transactional(readOnly = true)
    public Set<String> getProjectMemberStaffIds(String projectCode, String scope) {
        if (projectCode == null) {
            return Collections.emptySet();
        }
        if (SCOPE_ALL.equalsIgnoreCase(scope)) {
            return projectStaffRepository.findAllStaffIdsByProjectCode(projectCode);
        }
        return projectStaffRepository.findLeaderStaffIdsByProjectCode(projectCode);
    }

    private boolean isVisibleTo(String viewerStaffId, Message message) {
        if (viewerStaffId.equalsIgnoreCase(message.getSenderStaffId())) {
            return true;
        }
        String type = message.getRecipientType();
        if (TYPE_DIRECT.equalsIgnoreCase(type)) {
            return viewerStaffId.equalsIgnoreCase(message.getRecipientStaffId());
        }
        if (TYPE_BROADCAST.equalsIgnoreCase(type)) {
            return true;
        }
        if (TYPE_PROJECT.equalsIgnoreCase(type)) {
            return isProjectMember(viewerStaffId, message.getProjectCode(), message.getProjectGroupScope());
        }
        return false;
    }

    private void updateLatest(Map<String, MessageDto> map, String key, Message message, String viewerStaffId) {
        MessageDto dto = toDto(message, viewerStaffId);
        MessageDto existing = map.get(key);
        if (existing == null || dto.getCreatedAt().isAfter(existing.getCreatedAt())) {
            map.put(key, dto);
        }
    }

    private List<MessageDto> toDtos(List<Message> messages, String viewerStaffId) {
        if (messages == null || messages.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> messageIds = messages.stream().map(Message::getMessageId).collect(Collectors.toSet());
        List<MessageReadReceipt> receipts = readReceiptRepository.findByMessageIdIn(new ArrayList<>(messageIds));
        Set<Long> readByViewer = receipts.stream()
                .filter(r -> viewerStaffId != null && viewerStaffId.equalsIgnoreCase(r.getStaffId()))
                .map(MessageReadReceipt::getMessageId)
                .collect(Collectors.toSet());

        Map<String, Staff> staffCache = new HashMap<>();
        List<MessageDto> result = new ArrayList<>();
        for (Message message : messages) {
            MessageDto dto = toDto(message, viewerStaffId);
            dto.setReadByMe(readByViewer.contains(message.getMessageId())
                    || viewerStaffId != null && viewerStaffId.equalsIgnoreCase(message.getSenderStaffId()));
            dto.setSenderName(resolveStaffName(message.getSenderStaffId(), staffCache));
            if (TYPE_DIRECT.equalsIgnoreCase(message.getRecipientType())) {
                dto.setRecipientName(resolveStaffName(message.getRecipientStaffId(), staffCache));
            }
            result.add(dto);
        }
        return result;
    }

    private MessageDto toDto(Message message, String viewerStaffId) {
        return MessageDto.builder()
                .messageId(message.getMessageId())
                .senderStaffId(message.getSenderStaffId())
                .recipientType(message.getRecipientType())
                .recipientStaffId(message.getRecipientStaffId())
                .projectCode(message.getProjectCode())
                .content(message.getContent())
                .source(message.getSource())
                .category(message.getCategory())
                .referenceId(message.getReferenceId())
                .createdAt(message.getCreatedAt())
                .readAt(message.getReadAt())
                .projectGroupScope(message.getProjectGroupScope())
                .build();
    }

    private String resolveStaffName(String staffId, Map<String, Staff> cache) {
        if (staffId == null) {
            return null;
        }
        if (SOURCE_SYSTEM.equalsIgnoreCase(staffId)) {
            return "System";
        }
        Staff staff = cache.computeIfAbsent(staffId, id -> staffRepository.findById(id).orElse(null));
        return staff != null ? staff.getStaffName() : staffId;
    }

    private String resolveProjectGroupScope(String requestedScope) {
        if (!isProjectGroupScopeChoiceAllowed()) {
            return getDefaultProjectGroupScope();
        }
        if (SCOPE_ALL.equalsIgnoreCase(requestedScope)) {
            return SCOPE_ALL;
        }
        return SCOPE_LEADERSHIP;
    }

    private String getDefaultProjectGroupScope() {
        return paramRepository.findById(PARAM_CHAT_DEFAULT_SCOPE)
                .map(Param::getValue_string)
                .map(String::trim)
                .filter(value -> SCOPE_ALL.equalsIgnoreCase(value))
                .orElse(SCOPE_LEADERSHIP);
    }

    private boolean isProjectGroupScopeChoiceAllowed() {
        return paramRepository.findById(PARAM_CHAT_ALLOW_SCOPE_CHOICE)
                .map(Param::getValue_string)
                .map(value -> "1".equals(value.trim()) || "true".equalsIgnoreCase(value.trim()))
                .orElse(false);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }
}
