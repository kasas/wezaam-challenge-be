package com.wezaam.withdrawal.rest;

import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalScheduled;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import com.wezaam.withdrawal.repository.PaymentMethodRepository;
import com.wezaam.withdrawal.repository.UserRepository;
import com.wezaam.withdrawal.repository.WithdrawalRepository;
import com.wezaam.withdrawal.repository.WithdrawalScheduledRepository;
import com.wezaam.withdrawal.service.WithdrawalScheduledService;
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
    private final UserRepository userRepository;
    private final WithdrawalService withdrawalService;
    private final WithdrawalScheduledService withdrawalScheduledService;
    private final PaymentMethodRepository paymentMethodRepository;

    @PostMapping("/create-withdrawals")
    public ResponseEntity create(@RequestParam(name = "userId") Long userId,
                                 @RequestParam(name = "paymentMethodId") Long paymentMethodId,
                                 @RequestParam(name = "amount") Double amount,
                                 @RequestParam(name = "executeAt") String executeAt) {
        if (userId == null || paymentMethodId == null || amount == null || executeAt == null) {
            return new ResponseEntity("Required params are missing", HttpStatus.BAD_REQUEST);
        }
        if(!userRepository.findById(userId).isPresent()){
            return new ResponseEntity("User not found", HttpStatus.NOT_FOUND);
        }
        if (!paymentMethodRepository.findById(paymentMethodId).isPresent()) {
            return new ResponseEntity("Payment method not found", HttpStatus.NOT_FOUND);
        }

        Object body;
        if (executeAt.equals(WITHDRAWAL_EXEC_ASAP)) {
            body = withdrawalService.create(userId, paymentMethodId, amount);
        } else {
            body = withdrawalScheduledService.schedule(userId,paymentMethodId,amount, executeAt);
        }

        return new ResponseEntity(body, HttpStatus.OK);
    }

    @GetMapping("/find-all-withdrawals")
    public ResponseEntity findAll() {
        List<Withdrawal> withdrawals = withdrawalService.findAll();
        List<WithdrawalScheduled> withdrawalsScheduled = withdrawalScheduledService.findAll();
        List<Object> result = new ArrayList<>();
        result.addAll(withdrawals);
        result.addAll(withdrawalsScheduled);

        return new ResponseEntity(result, HttpStatus.OK);
    }
}
