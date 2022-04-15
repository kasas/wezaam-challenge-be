package com.wezaam.withdrawal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.Instant;

import static javax.persistence.GenerationType.IDENTITY;

@Entity(name = "scheduled_withdrawals")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalScheduled {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private Long transactionId;
    private Double amount;
    private Instant createdAt;
    private Instant executeAt;
    private Long userId;
    private Long paymentMethodId;
    @Enumerated(EnumType.STRING)
    private WithdrawalStatus status;

}
