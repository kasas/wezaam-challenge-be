package com.wezaam.withdrawal.provider.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity(name = "transaction")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private Double amount;
    private Long paymentMethodId;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

}
