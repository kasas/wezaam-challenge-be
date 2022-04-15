package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.config.NotificationMQConfig;
import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalScheduled;
import com.wezaam.withdrawal.model.dto.WithdrawalDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventsService {

    private final RabbitTemplate template;
    private final WithdrawalDTOFactory factory;
    @Async
    public void send(Withdrawal withdrawal) {
        // build and send an event in message queue async
        WithdrawalDTO dto = factory.create(withdrawal);
        template.convertAndSend(NotificationMQConfig.EXCHANGE, NotificationMQConfig.ROUTING_KEY, dto);
    }

    @Async
    public void send(WithdrawalScheduled withdrawal) {
        // build and send an event in message queue async
        WithdrawalDTO dto = factory.create(withdrawal);
        template.convertAndSend(NotificationMQConfig.EXCHANGE, NotificationMQConfig.ROUTING_KEY, dto);
    }
}
