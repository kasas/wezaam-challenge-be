package com.wezaam.withdrawal.provider.model.dto;

import com.wezaam.withdrawal.model.WithdrawalStatus;
import com.wezaam.withdrawal.model.WithdrawalType;
import com.wezaam.withdrawal.provider.model.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO implements Serializable {

    private Long id;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

}
