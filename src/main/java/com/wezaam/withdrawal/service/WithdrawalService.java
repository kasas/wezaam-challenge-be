package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.exception.TransactionException;
import com.wezaam.withdrawal.model.PaymentMethod;
import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalScheduled;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import com.wezaam.withdrawal.repository.PaymentMethodRepository;
import com.wezaam.withdrawal.repository.WithdrawalRepository;
import com.wezaam.withdrawal.repository.WithdrawalScheduledRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    private final WithdrawalProcessingService withdrawalProcessingService;
    private final PaymentMethodRepository paymentMethodRepository;
    private final EventsService eventsService;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public Withdrawal create(Long userId, Long paymentMethodId, Double amount){

        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setUserId(userId);
        withdrawal.setPaymentMethodId(paymentMethodId);
        withdrawal.setAmount(amount);
        withdrawal.setCreatedAt(Instant.now());
        withdrawal.setStatus(WithdrawalStatus.PENDING);

        create(withdrawal);

        return withdrawal;
    }
    public void create(Withdrawal withdrawal) {
        Withdrawal pendingWithdrawal = withdrawalRepository.save(withdrawal);

        executorService.submit(() -> {
            Optional<Withdrawal> savedWithdrawalOptional = withdrawalRepository.findById(pendingWithdrawal.getId());

            PaymentMethod paymentMethod;
            if (savedWithdrawalOptional.isPresent()) {
                paymentMethod = paymentMethodRepository.findById(savedWithdrawalOptional.get().getPaymentMethodId()).orElse(null);
            } else {
                paymentMethod = null;
            }

            if (savedWithdrawalOptional.isPresent() && paymentMethod != null) {
                Withdrawal savedWithdrawal = savedWithdrawalOptional.get();
                try {
                    var transactionId = withdrawalProcessingService.sendToProcessing(withdrawal.getAmount(), paymentMethod);
                    savedWithdrawal.setStatus(WithdrawalStatus.PROCESSING);
                    savedWithdrawal.setTransactionId(transactionId);
                    withdrawalRepository.save(savedWithdrawal);
                    eventsService.send(savedWithdrawal);
                } catch (Exception e) {
                    if (e instanceof TransactionException) {
                        savedWithdrawal.setStatus(WithdrawalStatus.FAILED);
                        withdrawalRepository.save(savedWithdrawal);
                        eventsService.send(savedWithdrawal);
                    } else {
                        savedWithdrawal.setStatus(WithdrawalStatus.INTERNAL_ERROR);
                        withdrawalRepository.save(savedWithdrawal);
                        eventsService.send(savedWithdrawal);
                    }
                }
            }
        });
    }

    public List<Withdrawal> findAll() {
        return withdrawalRepository.findAll();
    }

    public Optional<Withdrawal> findById(Long id) {
        return withdrawalRepository.findById(id);
    }
}
