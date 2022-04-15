package com.wezaam.withdrawal.repository;

import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalScheduled;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface WithdrawalScheduledRepository extends JpaRepository<WithdrawalScheduled, Long> {
    Optional<WithdrawalScheduled> findByTransactionId(Long id);
    List<WithdrawalScheduled> findAllByExecuteAtBefore(Instant date);

    List<WithdrawalScheduled> findAllByExecuteAtBeforeAndStatus(Instant date, WithdrawalStatus status);
}
