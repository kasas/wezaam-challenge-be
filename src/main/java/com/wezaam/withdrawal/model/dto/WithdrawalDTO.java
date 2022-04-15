package com.wezaam.withdrawal.model.dto;

import com.wezaam.withdrawal.model.WithdrawalStatus;
import com.wezaam.withdrawal.model.WithdrawalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalDTO implements Serializable {

    private Long id;
    private Long transactionId;
    private Double amount;
    private Long userId;
    private Long paymentMethodId;
    @Enumerated(EnumType.STRING)
    private WithdrawalStatus status;
    @Enumerated(EnumType.STRING)
    private WithdrawalType type;

}
