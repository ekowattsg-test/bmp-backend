package com.hcteol.jwt.backend.entities;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class MobileLogin {

    @Id
    private String loginKey;
    private String mobileNumber;
    @Column(name = "request_time", columnDefinition = "TIMESTAMP")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime requestTime;
    private String otp;
    @Column(name = "status", length = 50)
    @org.hibernate.annotations.ColumnDefault("'NEW'")
    private String status;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = "NEW";
        }
    }
}
