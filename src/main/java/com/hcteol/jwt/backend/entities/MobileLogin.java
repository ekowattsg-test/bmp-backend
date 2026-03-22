package com.hcteol.jwt.backend.entities;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Id;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.GeneratedValue;

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
    @Column(name = "status", columnDefinition = "varchar(50) default 'NEW'")
    private String status = "NEW";

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = "NEW";
        }
    }
}