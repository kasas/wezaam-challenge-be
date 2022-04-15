package com.wezaam.withdrawal.provider;

import com.wezaam.withdrawal.provider.model.Transaction;

import java.util.Optional;

public interface PaymentProvider {

    Optional<Transaction> create(Long paymentMethodId, Double amount);
    void process(Transaction transaction);
}
