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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WithdrawalControllerIntegrationTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    WithdrawalService withdrawalService;
    @Autowired
    WithdrawalScheduledService withdrawalScheduledService;
    @Autowired
    PaymentMethodRepository paymentMethodRepository;
    @Test
    void itShouldHaveEmptyWidthdrawals() {

        //given empty database
        WithdrawalController controller = new WithdrawalController(userRepository, withdrawalService, withdrawalScheduledService, paymentMethodRepository);

        //when
        ResponseEntity response = controller.findAll();

        //then
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        assertTrue(response.getBody() instanceof List);
        assertEquals(0, ((List<?>) response.getBody()).size(), "There are no withdrawal in the H2 DB");

    }

    @Test
    void itShouldHaveTheWithdrawalInserted() {
        //given empty database
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
    void itShouldHaveTheWithdrawalScheduledInserted() {
        //given empty database
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

    @Test
    void itShouldHaveBadRequestWhenUserIsNotSet() {
        //given empty database
        WithdrawalController controller = new WithdrawalController(userRepository, withdrawalService, withdrawalScheduledService, paymentMethodRepository);

        //when
        ResponseEntity response = controller.create(null, 1L, 100D, "2022-04-15T10:24:06.890598Z");

        //then
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }
    @Test
    void itShouldHaveBadRequestWhenUserIdNotCorrect() {
        //given empty database
        WithdrawalController controller = new WithdrawalController(userRepository, withdrawalService, withdrawalScheduledService, paymentMethodRepository);

        //when
        ResponseEntity response = controller.create(-1L, 1L, 100D, "2022-04-15T10:24:06.890598Z");

        //then
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }
    @Test
    void itShouldHaveBadRequestWhenPaymentMethodIdNotCorrect() {
        //given empty database
        WithdrawalController controller = new WithdrawalController(userRepository, withdrawalService, withdrawalScheduledService, paymentMethodRepository);

        //when
        ResponseEntity response = controller.create(1L, -1L, 100D, "2022-04-15T10:24:06.890598Z");

        //then
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }
}