package com.wezaam.withdrawal.provider.service;

import com.wezaam.withdrawal.config.NotificationMQConfig;
import com.wezaam.withdrawal.config.TransactionMQConfig;
import com.wezaam.withdrawal.provider.model.Transaction;
import com.wezaam.withdrawal.provider.model.dto.TransactionDTO;
import com.wezaam.withdrawal.service.WithdrawalDTOFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionEventService {
    private final RabbitTemplate template;
    @Async
    public void send(Transaction transaction) {
        // build and send an event in message queue async
        TransactionDTO dto = TransactionDTO.builder().id(transaction.getId()).status(transaction.getStatus()).build();
        template.convertAndSend(TransactionMQConfig.EXCHANGE, TransactionMQConfig.ROUTING_KEY, dto);
    }
}
