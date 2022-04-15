package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.exception.TransactionException;
import com.wezaam.withdrawal.model.PaymentMethod;
import com.wezaam.withdrawal.provider.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WithdrawalProcessingService {

    private final PaymentProvider paymentProvider;

    public Long sendToProcessing(Double amount, PaymentMethod paymentMethod) throws TransactionException {
        try {
            // call a payment provider
            var transaction = paymentProvider.create(paymentMethod.getId(), amount).orElseThrow();

            // in case a transaction can be process
            // it generates a transactionId and process the transaction async
            paymentProvider.process(transaction);

            // then return id generated (H2)
            return transaction.getId();
        }
        catch (Exception e){
            // otherwise it throws TransactionException
            throw new TransactionException();
        }
    }
}
