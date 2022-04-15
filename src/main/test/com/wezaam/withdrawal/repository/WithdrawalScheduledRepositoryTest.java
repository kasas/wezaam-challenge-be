package com.wezaam.withdrawal.repository;

import com.wezaam.withdrawal.model.WithdrawalScheduled;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class WithdrawalScheduledRepositoryTest {

    @Autowired
    private WithdrawalScheduledRepository repository;

    @Test
    void itShouldTestThereIsWithdrawalScheduled() {
        // given
        Instant now = Instant.now();
        WithdrawalScheduled item = WithdrawalScheduled.builder().id(System.nanoTime())
                .createdAt(now)
                .executeAt(now)
                .amount(100d)
                .paymentMethodId(1L)
                .userId(1L).status(WithdrawalStatus.PENDING).build();

        repository.save(item);

        // when

        List<WithdrawalScheduled> items = repository.findAllByExecuteAtBefore(Instant.now());

        // then
        assertNotNull(items);
        assertEquals(1, items.size(), "It has expected number of scheduled withdrawals");

    }
}