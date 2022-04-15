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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class WithdrawalScheduledService {

    private final WithdrawalScheduledRepository withdrawalScheduledRepository;
    private final WithdrawalProcessingService withdrawalProcessingService;
    private final PaymentMethodRepository paymentMethodRepository;
    private final EventsService eventsService;

    public WithdrawalScheduled schedule(Long userId, Long paymentMethodId, Double amount, Instant executedAt){

        WithdrawalScheduled withdrawal = WithdrawalScheduled.builder()
                .status(WithdrawalStatus.PENDING)
                .userId(userId)
                .paymentMethodId(paymentMethodId)
                .createdAt(Instant.now())
                .executeAt(executedAt)
                .amount(amount)
                .build();

        schedule(withdrawal);

        return withdrawal;
    }


    public void schedule(WithdrawalScheduled withdrawalScheduled) {
        withdrawalScheduledRepository.save(withdrawalScheduled);
    }

    @Scheduled(fixedDelay = 5000)
    public void run() {
        withdrawalScheduledRepository.findAllByExecuteAtBeforeAndStatus(Instant.now(), WithdrawalStatus.PENDING)
                .forEach(this::processScheduled);
    }

    private void processScheduled(WithdrawalScheduled withdrawal) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(withdrawal.getPaymentMethodId()).orElse(null);
        if (paymentMethod != null) {
            try {
                var transactionId = withdrawalProcessingService.sendToProcessing(withdrawal.getAmount(), paymentMethod);
                withdrawal.setTransactionId(transactionId);
                withdrawal.setStatus(WithdrawalStatus.PROCESSING);
                withdrawalScheduledRepository.save(withdrawal);
            } catch (Exception e) {
                if (e instanceof TransactionException) {
                    withdrawal.setStatus(WithdrawalStatus.FAILED);
                    withdrawalScheduledRepository.save(withdrawal);
                } else {
                    withdrawal.setStatus(WithdrawalStatus.INTERNAL_ERROR);
                    withdrawalScheduledRepository.save(withdrawal);
                }
            }
            //We send the final status of the withdrawal
            eventsService.send(withdrawal);
        }
    }

    public List<WithdrawalScheduled> findAll() {
        return withdrawalScheduledRepository.findAll();
    }

    public Optional<WithdrawalScheduled> findById(Long id) {
        return withdrawalScheduledRepository.findById(id);
    }
}
