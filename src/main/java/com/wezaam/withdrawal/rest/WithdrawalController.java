package com.wezaam.withdrawal.rest;

import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalScheduled;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import com.wezaam.withdrawal.repository.PaymentMethodRepository;
import com.wezaam.withdrawal.repository.WithdrawalRepository;
import com.wezaam.withdrawal.repository.WithdrawalScheduledRepository;
import com.wezaam.withdrawal.service.WithdrawalService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Api
@RestController
@RequiredArgsConstructor
public class WithdrawalController {
    public static final String WITHDRAWAL_EXEC_ASAP = "ASAP";
    private final UserController userController;
    private final WithdrawalService withdrawalService;
    private final PaymentMethodRepository paymentMethodRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final WithdrawalScheduledRepository withdrawalScheduledRepository;

    @PostMapping("/create-withdrawals")
    public ResponseEntity create(HttpServletRequest request, @RequestParam(name = "userId") Long userId,
                                 @RequestParam(name = "paymentMethodId") Long paymentMethodId,
                                 @RequestParam(name = "amount") Double amount,
                                 @RequestParam(name = "executeAt") String executeAt) {
        if (userId == null || paymentMethodId == null || amount == null || executeAt == null) {
            return new ResponseEntity("Required params are missing", HttpStatus.BAD_REQUEST);
        }
        try {
            userController.findById(userId);
        } catch (Exception e) {
            return new ResponseEntity("User not found", HttpStatus.NOT_FOUND);
        }
        if (!paymentMethodRepository.findById(paymentMethodId).isPresent()) {
            return new ResponseEntity("Payment method not found", HttpStatus.NOT_FOUND);
        }

        Object body;
        if (executeAt.equals(WITHDRAWAL_EXEC_ASAP)) {
            Withdrawal withdrawal = new Withdrawal();
            withdrawal.setUserId(userId);
            withdrawal.setPaymentMethodId(paymentMethodId);
            withdrawal.setAmount(amount);
            withdrawal.setCreatedAt(Instant.now());
            withdrawal.setStatus(WithdrawalStatus.PENDING);
            withdrawalService.create(withdrawal);
            body = withdrawal;
        } else {
            WithdrawalScheduled withdrawalScheduled = new WithdrawalScheduled();
            withdrawalScheduled.setUserId(userId);
            withdrawalScheduled.setPaymentMethodId(paymentMethodId);
            withdrawalScheduled.setAmount(amount);
            withdrawalScheduled.setCreatedAt(Instant.now());
            withdrawalScheduled.setExecuteAt(Instant.parse(executeAt));
            withdrawalScheduled.setStatus(WithdrawalStatus.PENDING);
            withdrawalService.schedule(withdrawalScheduled);
            body = withdrawalScheduled;
        }

        return new ResponseEntity(body, HttpStatus.OK);
    }

    @GetMapping("/find-all-withdrawals")
    public ResponseEntity findAll() {
        List<Withdrawal> withdrawals = withdrawalRepository.findAll();
        List<WithdrawalScheduled> withdrawalsScheduled = withdrawalScheduledRepository.findAll();
        List<Object> result = new ArrayList<>();
        result.addAll(withdrawals);
        result.addAll(withdrawalsScheduled);

        return new ResponseEntity(result, HttpStatus.OK);
    }
}
