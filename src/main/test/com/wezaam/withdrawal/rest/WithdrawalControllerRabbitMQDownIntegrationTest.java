package com.wezaam.withdrawal.rest;

import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalScheduled;
import com.wezaam.withdrawal.repository.PaymentMethodRepository;
import com.wezaam.withdrawal.repository.UserRepository;
import com.wezaam.withdrawal.service.WithdrawalScheduledService;
import com.wezaam.withdrawal.service.WithdrawalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        properties = { "spring.rabbitmq.addresses=other:5672" }
)
class WithdrawalControllerRabbitMQDownIntegrationTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    WithdrawalService withdrawalService;
    @Autowired
    WithdrawalScheduledService withdrawalScheduledService;
    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Test
    void itShouldHaveTheWithdrawalInsertedWhenRabbitMQIsDown() {
        //given initial database
        WithdrawalController controller = new WithdrawalController(userRepository, withdrawalService, withdrawalScheduledService, paymentMethodRepository);

        //when
        ResponseEntity response = controller.create( 1L, 1L, 100D, controller.WITHDRAWAL_EXEC_ASAP);

        Object w = response.getBody();
        //then
        assertTrue(w instanceof Withdrawal);
        Optional<Withdrawal> itemById = withdrawalService.findById(((Withdrawal) w).getId());
        assertTrue(itemById.isPresent());
        assertEquals(w, itemById.orElse(null));

    }

    @Test
    void itShouldHaveTheWithdrawalScheduledInsertedWhenRabbitMQIsDown() {
        //given initial database
        WithdrawalController controller = new WithdrawalController(userRepository, withdrawalService, withdrawalScheduledService, paymentMethodRepository);

        //when
        ResponseEntity response = controller.create(1L, 1L, 100D, "2022-04-15T10:24:06.890598Z");

        Object w = response.getBody();
        //then
        assertTrue(w instanceof WithdrawalScheduled);
        Optional<WithdrawalScheduled> itemById = withdrawalScheduledService.findById(((WithdrawalScheduled) w).getId());
        assertTrue(itemById.isPresent());
        assertEquals(w, itemById.orElse(null));

    }


}