package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalScheduled;
import com.wezaam.withdrawal.model.WithdrawalType;
import com.wezaam.withdrawal.model.dto.WithdrawalDTO;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WithdrawalDTOFactory {

    public WithdrawalDTO create(Withdrawal item){
        return WithdrawalDTO.builder()
                .id(item.getId())
                .transactionId(item.getTransactionId())
                .amount(item.getAmount())
                .paymentMethodId(item.getPaymentMethodId())
                .status(item.getStatus())
                .type(WithdrawalType.ASAP)
                .userId(item.getUserId())
                .build();
    }

    public WithdrawalDTO create(WithdrawalScheduled item){
        return WithdrawalDTO.builder()
                .id(item.getId())
                .transactionId(item.getTransactionId())
                .amount(item.getAmount())
                .paymentMethodId(item.getPaymentMethodId())
                .status(item.getStatus())
                .type(WithdrawalType.SCHEDULED)
                .userId(item.getUserId())
                .build();
    }
}
