package com.wezaam.withdrawal.rest;

import com.wezaam.withdrawal.config.NotificationMQConfig;
import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalScheduled;
import com.wezaam.withdrawal.model.dto.WithdrawalDTO;
import com.wezaam.withdrawal.repository.PaymentMethodRepository;
import com.wezaam.withdrawal.repository.UserRepository;
import com.wezaam.withdrawal.service.WithdrawalScheduledService;
import com.wezaam.withdrawal.service.WithdrawalService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class WithdrawalControllerRabbitMQIntegrationTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    WithdrawalService withdrawalService;
    @Autowired
    WithdrawalScheduledService withdrawalScheduledService;
    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    private CountDownLatch lock = new CountDownLatch(1);

    @Test
    void itShouldHaveTheWithdrawalInsertedAndNotificationQueued() {
        //given initial database and RabbitMQ started
        WithdrawalController controller = new WithdrawalController(userRepository, withdrawalService, withdrawalScheduledService, paymentMethodRepository);

        //when
        ResponseEntity response = controller.create(1L, 1L, 100D, controller.WITHDRAWAL_EXEC_ASAP);

        Object w = response.getBody();
        //then
        assertTrue(w instanceof Withdrawal);
        Optional<Withdrawal> itemById = withdrawalService.findById(((Withdrawal) w).getId());
        assertTrue(itemById.isPresent());
        assertEquals(w, itemById.orElse(null));


        try {
            lock.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }

        boolean messageInQueue = isMessagePublishedInQueue(w).booleanValue();

        assertTrue(messageInQueue);

    }

    @Test
    void itShouldHaveTheWithdrawalScheduledInsertedAndNotificationQueued() {
        //given initial database and RabbitMQ started
        WithdrawalController controller = new WithdrawalController(userRepository, withdrawalService, withdrawalScheduledService, paymentMethodRepository);

        //when
        ResponseEntity response = controller.create(1L, 1L, 100D, "2022-04-15T10:24:06.890598Z");

        Object w = response.getBody();
        //then
        assertTrue(w instanceof WithdrawalScheduled);
        Optional<WithdrawalScheduled> itemById = withdrawalScheduledService.findById(((WithdrawalScheduled) w).getId());
        assertTrue(itemById.isPresent());
        assertEquals(w, itemById.orElse(null));


        try {
            lock.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }

        boolean messageInQueue = isMessagePublishedInQueue(w).booleanValue();

        assertTrue(messageInQueue);
    }


    private Boolean isMessagePublishedInQueue(Object item) {
        /*var queueMessageCount = rabbitTemplate.execute(it -> {
            return it.queueDeclare(NotificationMQConfig.QUEUE, true, false, false, null);
        }).getMessageCount();*/

        var queuedMessage = rabbitTemplate.receiveAndConvert(NotificationMQConfig.QUEUE);
        return queuedMessage instanceof WithdrawalDTO;

    }


}