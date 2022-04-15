package com.wezaam.withdrawal.consumers;

import com.wezaam.withdrawal.config.NotificationMQConfig;
import com.wezaam.withdrawal.config.TransactionMQConfig;
import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalScheduled;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import com.wezaam.withdrawal.model.dto.WithdrawalDTO;
import com.wezaam.withdrawal.provider.model.TransactionStatus;
import com.wezaam.withdrawal.provider.model.dto.TransactionDTO;
import com.wezaam.withdrawal.repository.WithdrawalRepository;
import com.wezaam.withdrawal.repository.WithdrawalScheduledRepository;
import com.wezaam.withdrawal.service.EventsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionQueueConsumer {

    private final WithdrawalScheduledRepository withdrawalScheduledRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final EventsService eventsService;

    @RabbitListener(queues = TransactionMQConfig.QUEUE)
    public void receiveData(TransactionDTO item) {

        log.info(String.format("Transaction: %o with status %s", item.getId(),  item.getStatus()));

        Optional<Withdrawal> withdrawal = withdrawalRepository.findByTransactionId(item.getId());
        if(withdrawal.isPresent()){
            withdrawal.get().setStatus(processTransactionStatus(item.getStatus()));
            eventsService.send(withdrawal.get());
        }

        Optional<WithdrawalScheduled> withdrawalScheduled = withdrawalScheduledRepository.findByTransactionId(item.getId());
        if(withdrawalScheduled.isPresent()){
            withdrawalScheduled.get().setStatus(processTransactionStatus(item.getStatus()));
            eventsService.send(withdrawalScheduled.get());
        }

        log.info("Transaction processed...");
    }

    private WithdrawalStatus processTransactionStatus(TransactionStatus status) {
        switch (status){
            case SUCCESS -> {
                return WithdrawalStatus.SUCCESS;
            }
            case FAILED -> {return WithdrawalStatus.FAILED;}
            default -> {return WithdrawalStatus.PENDING;}
        }
    }
}
