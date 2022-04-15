package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalScheduled;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import com.wezaam.withdrawal.model.dto.WithdrawalDTO;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.*;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        properties = { "spring.rabbitmq.addresses=other:5672" }
)
class EventsServiceTest {

    @Autowired
    RabbitTemplate template;
    @Autowired
    WithdrawalDTOFactory factory;
    private CountDownLatch lock = new CountDownLatch(1);
    @Test
    void itShouldCheckWithdrawalIsInQueueWhenRabbitMQIsDown() {
        //given
        var eventService = new EventsService(template, factory);
        Withdrawal item = Withdrawal.builder().amount(100D).createdAt(Instant.now()).paymentMethodId(1L).userId(1L).transactionId(1L).id(1L).status(WithdrawalStatus.PROCESSING).build();
        //when
        eventService.send(item);
        //then
        try {
            lock.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }

        long itemsInQueue = eventService.getQueue().stream().count();

        assertTrue(itemsInQueue == 1);
    }

    @Test
    void itShouldCheckWithdrawalScheduledIsInQueueWhenRabbitMQIsDown() {
        //given
        var eventService = new EventsService(template, factory);
        WithdrawalScheduled item = WithdrawalScheduled.builder().amount(100D).createdAt(Instant.now()).executeAt(Instant.now()).paymentMethodId(1L).userId(1L).transactionId(1L).id(1L).status(WithdrawalStatus.PROCESSING).build();
        //when
        eventService.send(item);
        //then
        try {
            lock.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }

        long itemsInQueue = eventService.getQueue().stream().count();

        assertTrue(itemsInQueue == 1);
    }
}