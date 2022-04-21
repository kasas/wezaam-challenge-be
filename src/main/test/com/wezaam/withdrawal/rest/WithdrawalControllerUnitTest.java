package com.wezaam.withdrawal.rest;

import com.wezaam.withdrawal.model.PaymentMethod;
import com.wezaam.withdrawal.model.User;
import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalScheduled;
import com.wezaam.withdrawal.repository.PaymentMethodRepository;
import com.wezaam.withdrawal.repository.UserRepository;
import com.wezaam.withdrawal.service.WithdrawalScheduledService;
import com.wezaam.withdrawal.service.WithdrawalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class WithdrawalControllerUnitTest
{

    @Mock
    UserRepository userRepository;
    @Mock
    WithdrawalService withdrawalService;
    @Mock
    WithdrawalScheduledService withdrawalScheduledService;
    @Mock
    PaymentMethodRepository paymentMethodRepository;
    @Test
    void itShouldHaveEmptyWithdrawals() {

        //given initial database
        Mockito.when(withdrawalService.findAll()).thenReturn(new ArrayList<>());
        Mockito.when(withdrawalScheduledService.findAll()).thenReturn(new ArrayList<>());
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
        //given initial database
        Withdrawal withdrawal = Withdrawal.builder().id(1L).build();
        PaymentMethod paymentMethod = PaymentMethod.builder().id(1L).build();
        User user = User.builder().id(1L).maxWithdrawalAmount(Double.MAX_VALUE).build();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(withdrawalService.create(1L, 1L, 100D)).thenReturn(withdrawal);
        Mockito.when(withdrawalService.findById(1L)).thenReturn(Optional.of(withdrawal));
        Mockito.when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(paymentMethod));
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
        //given initial database
        WithdrawalScheduled withdrawal = WithdrawalScheduled.builder().id(1L).build();
        PaymentMethod paymentMethod = PaymentMethod.builder().id(1L).build();
        User user = User.builder().id(1L).maxWithdrawalAmount(Double.MAX_VALUE).build();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(withdrawalScheduledService.schedule(1L, 1L, 100D, Instant.parse("2022-04-15T10:24:06.890598Z"))).thenReturn(withdrawal);
        Mockito.when(withdrawalScheduledService.findById(1L)).thenReturn(Optional.of(withdrawal));
        Mockito.when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(paymentMethod));
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
    void itShouldHaveBadRequestWhenSomeParameterIsNotSet() {
        //given initial database
        WithdrawalController controller = new WithdrawalController(userRepository, withdrawalService, withdrawalScheduledService, paymentMethodRepository);

        //when
        ResponseEntity response1 = controller.create(null, 1L, 100D, "2022-04-15T10:24:06.890598Z");
        ResponseEntity response2 = controller.create(1L, null, 100D, "2022-04-15T10:24:06.890598Z");
        ResponseEntity response3 = controller.create(1L, 1L, null, "2022-04-15T10:24:06.890598Z");
        ResponseEntity response4 = controller.create(1L, 1L, 100D, null);

        //then
        assertEquals(HttpStatus.BAD_REQUEST,response1.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST,response2.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST,response3.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST,response4.getStatusCode());
    }
    @Test
    void itShouldHaveNotFoundWhenUserIdNotCorrect() {
        //given initial database
        Mockito.when(userRepository.findById(-1L)).thenReturn(Optional.ofNullable(null));

        WithdrawalController controller = new WithdrawalController(userRepository, withdrawalService, withdrawalScheduledService, paymentMethodRepository);

        //when
        ResponseEntity response = controller.create(-1L, 1L, 100D, "2022-04-15T10:24:06.890598Z");

        //then
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }
    @Test
    void itShouldHaveNotFoundWhenPaymentMethodIdNotCorrect() {
        //given initial database
        User user = User.builder().id(1L).maxWithdrawalAmount(Double.MAX_VALUE).build();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(paymentMethodRepository.findById(-1L)).thenReturn(Optional.ofNullable(null));
        WithdrawalController controller = new WithdrawalController(userRepository, withdrawalService, withdrawalScheduledService, paymentMethodRepository);

        //when
        ResponseEntity response = controller.create(1L, -1L, 100D, "2022-04-15T10:24:06.890598Z");

        //then
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    void itShouldHaveBadRequestWhenExecuteAtNotCorrect() {
        //given initial database
        User user = User.builder().id(1L).maxWithdrawalAmount(Double.MAX_VALUE).build();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        WithdrawalScheduled withdrawal = WithdrawalScheduled.builder().id(1L).build();
        PaymentMethod paymentMethod = PaymentMethod.builder().id(1L).build();
        Mockito.when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(paymentMethod));
        WithdrawalController controller = new WithdrawalController(userRepository, withdrawalService, withdrawalScheduledService, paymentMethodRepository);

        //when
        ResponseEntity response = controller.create(1L, 1L, 100D, "2022-04-15");

        //then
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    void itShouldHaveBadRequestWhenAmountExceedMaximum() {
        //given initial database
        User user = User.builder().id(1L).maxWithdrawalAmount(Double.MIN_VALUE).build();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        WithdrawalScheduled withdrawal = WithdrawalScheduled.builder().id(1L).build();
        PaymentMethod paymentMethod = PaymentMethod.builder().id(1L).build();
        Mockito.when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(paymentMethod));
        WithdrawalController controller = new WithdrawalController(userRepository, withdrawalService, withdrawalScheduledService, paymentMethodRepository);

        //when
        ResponseEntity response = controller.create(1L, 1L, Double.MAX_VALUE, "ASAP");

        //then
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }
}
