package com.hcteol.jwt.backend.entities;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Param {
    @Id
    private String param_key;
    private String value_string;
    private Long value_long;
    private BigDecimal value_decimal;
}
