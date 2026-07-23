package com.hcteol.jwt.backend.services;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.config.TvAuthProperties;
import com.hcteol.jwt.backend.config.UserAuthenticationProvider;
import com.hcteol.jwt.backend.dtos.TvSessionApproveRequest;
import com.hcteol.jwt.backend.dtos.TvSessionApproveResponse;
import com.hcteol.jwt.backend.dtos.TvSessionCreateRequest;
import com.hcteol.jwt.backend.dtos.TvSessionCreateResponse;
import com.hcteol.jwt.backend.dtos.TvSessionExchangeRequest;
import com.hcteol.jwt.backend.dtos.TvSessionExchangeResponse;
import com.hcteol.jwt.backend.dtos.TvSessionStatusResponse;
import com.hcteol.jwt.backend.dtos.UserDto;
import com.hcteol.jwt.backend.entities.Project;
import com.hcteol.jwt.backend.entities.TvScreenSession;
import com.hcteol.jwt.backend.exceptions.AppException;
import com.hcteol.jwt.backend.repositories.ProjectRepository;
import com.hcteol.jwt.backend.repositories.TvScreenSessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TvAuthService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_EXCHANGED = "EXCHANGED";
    private static final String STATUS_EXPIRED = "EXPIRED";
    private static final long DEFAULT_CHALLENGE_EXPIRY_SECONDS = 120;
    private static final long DEFAULT_SESSION_MAX_SECONDS = 28800;
    private static final long DEFAULT_EXCHANGE_CODE_EXPIRY_SECONDS = 60;
    private static final long DEFAULT_POLL_INTERVAL_SECONDS = 2;
    private static final long DEFAULT_REFRESH_INTERVAL_SECONDS = 30;
    private static final String DEFAULT_DESTINATION_URL = "/tv/projects";
    private static final String DEFAULT_QR_SCHEME_BASE = "bmp://tv-auth?code=";
    private static final ZoneId RESPONSE_ZONE = ZoneId.systemDefault();

    private final TvScreenSessionRepository tvScreenSessionRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final UserAuthenticationProvider userAuthenticationProvider;
    private final TvAuthProperties tvAuthProperties;

    @Transactional
    public TvSessionCreateResponse createSession(TvSessionCreateRequest request) {
        LocalDateTime now = LocalDateTime.now();
        long challengeExpirySeconds = sanitizePositive(tvAuthProperties.getChallengeExpirySeconds(),
                DEFAULT_CHALLENGE_EXPIRY_SECONDS);
        long sessionMaxSeconds = sanitizePositive(tvAuthProperties.getSessionMaxSeconds(),
                DEFAULT_SESSION_MAX_SECONDS);
        long pollIntervalSeconds = sanitizePositive(tvAuthProperties.getPollIntervalSeconds(),
                DEFAULT_POLL_INTERVAL_SECONDS);
        String qrSchemeBase = sanitizeString(tvAuthProperties.getQrSchemeBase(), DEFAULT_QR_SCHEME_BASE);

        TvScreenSession session = new TvScreenSession();
        session.setSessionCode(generateUniqueSessionCode());
        session.setPin(generatePin());
        session.setStatus(STATUS_PENDING);
        session.setCreatedAt(now);
        session.setChallengeExpiresAt(now.plusSeconds(challengeExpirySeconds));
        session.setSessionExpiresAt(now.plusSeconds(sessionMaxSeconds));
        session.setDestinationUrl(resolveDestinationUrl(request == null ? null : request.getDestinationUrl()));

        TvScreenSession saved = tvScreenSessionRepository.save(session);
        return new TvSessionCreateResponse(
                saved.getSessionCode(),
                saved.getPin(),
                qrSchemeBase + saved.getSessionCode(),
                toOffsetDateTime(saved.getChallengeExpiresAt()),
                pollIntervalSeconds);
    }

    @Transactional
    public TvSessionApproveResponse approveSession(TvSessionApproveRequest request, UserDto approver) {
        if (request == null || request.getSessionCode() == null || request.getSessionCode().isBlank()) {
            throw new AppException("sessionCode is required", HttpStatus.BAD_REQUEST);
        }
        if (approver == null || approver.getLogin() == null || approver.getLogin().isBlank()) {
            throw new AppException("Approver is required", HttpStatus.UNAUTHORIZED);
        }

        TvScreenSession session = tvScreenSessionRepository.findBySessionCode(request.getSessionCode().trim())
                .orElseThrow(() -> new AppException("TV session not found", HttpStatus.NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        refreshExpiredState(session, now);

        if (!STATUS_PENDING.equals(session.getStatus())) {
            throw new AppException("TV session is not pending", HttpStatus.CONFLICT);
        }
        if (session.getChallengeExpiresAt() != null && now.isAfter(session.getChallengeExpiresAt())) {
            session.setStatus(STATUS_EXPIRED);
            tvScreenSessionRepository.save(session);
            throw new AppException("TV session challenge expired", HttpStatus.GONE);
        }

        if (tvAuthProperties.isRequirePin()) {
            String pin = request.getPin() == null ? "" : request.getPin().trim();
            if (!Objects.equals(pin, session.getPin())) {
                throw new AppException("Invalid PIN", HttpStatus.UNAUTHORIZED);
            }
        }

        session.setApprovedByLogin(approver.getLogin());
        session.setApprovedAt(now);
        session.setStatus(STATUS_APPROVED);
        session.setExchangeCode(generateUniqueExchangeCode());
        session.setExchangeExpiresAt(now.plusSeconds(sanitizePositive(tvAuthProperties.getExchangeCodeExpirySeconds(),
                DEFAULT_EXCHANGE_CODE_EXPIRY_SECONDS)));
        session.setDestinationUrl(resolveDestinationUrl(request.getDestinationUrl()));

        TvScreenSession saved = tvScreenSessionRepository.save(session);
        return new TvSessionApproveResponse(saved.getSessionCode(), saved.getStatus(),
                toOffsetDateTime(saved.getApprovedAt()),
                toOffsetDateTime(saved.getSessionExpiresAt()));
    }

    @Transactional
    public TvSessionStatusResponse getSessionStatus(String sessionCode) {
        if (sessionCode == null || sessionCode.isBlank()) {
            throw new AppException("sessionCode is required", HttpStatus.BAD_REQUEST);
        }

        TvScreenSession session = tvScreenSessionRepository.findBySessionCode(sessionCode.trim())
                .orElseThrow(() -> new AppException("TV session not found", HttpStatus.NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        refreshExpiredState(session, now);
        tvScreenSessionRepository.save(session);

        String exchangeCode = STATUS_APPROVED.equals(session.getStatus()) ? session.getExchangeCode() : null;
        return new TvSessionStatusResponse(
                session.getSessionCode(),
                session.getStatus(),
                exchangeCode,
                session.getDestinationUrl(),
                toOffsetDateTime(session.getChallengeExpiresAt()),
                toOffsetDateTime(session.getSessionExpiresAt()));
    }

    @Transactional
    public TvSessionExchangeResponse exchangeToken(TvSessionExchangeRequest request) {
        if (request == null || request.getExchangeCode() == null || request.getExchangeCode().isBlank()) {
            throw new AppException("exchangeCode is required", HttpStatus.BAD_REQUEST);
        }

        TvScreenSession session = tvScreenSessionRepository.findByExchangeCode(request.getExchangeCode().trim())
                .orElseThrow(() -> new AppException("Exchange code not found", HttpStatus.NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        refreshExpiredState(session, now);

        if (!STATUS_APPROVED.equals(session.getStatus())) {
            throw new AppException("TV session is not ready for exchange", HttpStatus.CONFLICT);
        }
        if (session.getExchangeExpiresAt() == null || now.isAfter(session.getExchangeExpiresAt())) {
            session.setStatus(STATUS_EXPIRED);
            tvScreenSessionRepository.save(session);
            throw new AppException("Exchange code expired", HttpStatus.GONE);
        }

        UserDto approver = userService.findByLogin(session.getApprovedByLogin());
        String token = userAuthenticationProvider.createToken(approver);

        session.setStatus(STATUS_EXCHANGED);
        session.setExchangedAt(now);
        session.setExchangeCode(null);
        session.setExchangeExpiresAt(null);
        tvScreenSessionRepository.save(session);

        List<String> projectCodes = projectRepository.findAll().stream()
                .map(Project::getProjectCode)
                .filter(Objects::nonNull)
                .toList();

        return new TvSessionExchangeResponse(
                token,
                session.getDestinationUrl(),
                toOffsetDateTime(session.getSessionExpiresAt()),
                sanitizePositive(tvAuthProperties.getRefreshIntervalSeconds(), DEFAULT_REFRESH_INTERVAL_SECONDS),
                projectCodes);
    }

    private OffsetDateTime toOffsetDateTime(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.atZone(RESPONSE_ZONE).toOffsetDateTime();
    }

    private void refreshExpiredState(TvScreenSession session, LocalDateTime now) {
        if (session == null || now == null) {
            return;
        }
        if (session.getSessionExpiresAt() != null && now.isAfter(session.getSessionExpiresAt())) {
            session.setStatus(STATUS_EXPIRED);
            session.setExchangeCode(null);
            session.setExchangeExpiresAt(null);
            return;
        }
        if (STATUS_PENDING.equals(session.getStatus())
                && session.getChallengeExpiresAt() != null
                && now.isAfter(session.getChallengeExpiresAt())) {
            session.setStatus(STATUS_EXPIRED);
            session.setExchangeCode(null);
            session.setExchangeExpiresAt(null);
        }
    }

    private String resolveDestinationUrl(String preferredDestination) {
        if (preferredDestination == null || preferredDestination.isBlank()) {
            return sanitizeString(tvAuthProperties.getDefaultDestinationUrl(), DEFAULT_DESTINATION_URL);
        }
        return preferredDestination.trim();
    }

    private long sanitizePositive(long value, long fallback) {
        return value > 0 ? value : fallback;
    }

    private String sanitizeString(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private String generateUniqueSessionCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            String candidate = "TV-" + randomAlphaNumeric(24);
            Optional<TvScreenSession> existing = tvScreenSessionRepository.findBySessionCode(candidate);
            if (existing.isEmpty()) {
                return candidate;
            }
        }
        throw new AppException("Unable to allocate session code", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String generateUniqueExchangeCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            String candidate = "EX-" + randomAlphaNumeric(32);
            Optional<TvScreenSession> existing = tvScreenSessionRepository.findByExchangeCode(candidate);
            if (existing.isEmpty()) {
                return candidate;
            }
        }
        throw new AppException("Unable to allocate exchange code", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String generatePin() {
        int pin = ThreadLocalRandom.current().nextInt(1000, 10000);
        return String.valueOf(pin);
    }

    private String randomAlphaNumeric(int length) {
        final char[] chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = ThreadLocalRandom.current().nextInt(chars.length);
            builder.append(chars[idx]);
        }
        return builder.toString();
    }
}
