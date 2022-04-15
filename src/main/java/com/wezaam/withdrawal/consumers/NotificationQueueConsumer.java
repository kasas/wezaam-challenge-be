package com.wezaam.withdrawal.consumers;

import com.wezaam.withdrawal.config.NotificationMQConfig;
import com.wezaam.withdrawal.model.dto.WithdrawalDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

//@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationQueueConsumer {

    @RabbitListener(queues = NotificationMQConfig.QUEUE)
    public void receiveData(WithdrawalDTO item) {

        log.info(String.format("Transaction: %o for withdrawal: %o of type %s with status %s", item.getTransactionId(), item.getId(), item.getType(), item.getStatus()));

        log.info("Notification could be processed...");
    }
}
