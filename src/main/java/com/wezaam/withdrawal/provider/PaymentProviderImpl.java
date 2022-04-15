package com.wezaam.withdrawal.provider;

import com.wezaam.withdrawal.provider.model.Transaction;
import com.wezaam.withdrawal.provider.model.TransactionStatus;
import com.wezaam.withdrawal.provider.repository.TransactionRepository;
import com.wezaam.withdrawal.provider.service.TransactionEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PaymentProviderImpl implements PaymentProvider{

    private final TransactionRepository repository;
    private final TransactionEventService eventService;

    @Override
    public Optional<Transaction> create(Long paymentMethodId, Double amount) {
        Transaction item = Transaction.builder().amount(amount).paymentMethodId(paymentMethodId).status(TransactionStatus.PENDING).build();
        return Optional.of(repository.save(item));
    }

    @Override
    @Async
    public void process(Transaction transaction) {
        //In order to assure async we wait 2 seconds as if it is doing some checks...
        try { Thread.sleep(2000);}
        catch (Exception e){}

        //We simple change the status of the transaction
        transaction.setStatus(TransactionStatus.SUCCESS);

        //We could send the status of the transaction in a Queue if needed for example to notify the Transaction was succesful or not.
        // It is not the aim of this challenge
        repository.save(transaction);

        eventService.send(transaction);


    }
}
