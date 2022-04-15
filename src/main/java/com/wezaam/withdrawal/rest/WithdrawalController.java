package com.wezaam.withdrawal.rest;

import com.wezaam.withdrawal.model.User;
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
import java.util.Optional;

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

        Optional<User> user = userRepository.findById(userId);
        if(!user.isPresent()){
            return new ResponseEntity("User not found", HttpStatus.NOT_FOUND);
        }
        if (!paymentMethodRepository.findById(paymentMethodId).isPresent()) {
            return new ResponseEntity("Payment method not found", HttpStatus.NOT_FOUND);
        }
        if (user.get().getMaxWithdrawalAmount().doubleValue() < amount) {
            return new ResponseEntity("Amount exceed maxWithdrawalAmount of User", HttpStatus.BAD_REQUEST);
        }

        Object body;
        if (executeAt.equals(WITHDRAWAL_EXEC_ASAP)) {
            body = withdrawalService.create(userId, paymentMethodId, amount);
        } else {
            try {
                Instant executeAtInstant = Instant.parse(executeAt);
                body = withdrawalScheduledService.schedule(userId, paymentMethodId, amount, executeAtInstant);
            }
            catch (Exception e){
                return new ResponseEntity("Incorrect ExecutedAt Date", HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity(body, HttpStatus.OK);
    }

    @GetMapping("/find-all-withdrawals")
    public ResponseEntity findAll() {
        List<Object> result = new ArrayList<>();
        result.addAll(withdrawalService.findAll());
        result.addAll(withdrawalScheduledService.findAll());
        return new ResponseEntity(result, HttpStatus.OK);
    }
}
