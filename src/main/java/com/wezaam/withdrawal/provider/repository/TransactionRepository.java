package com.wezaam.withdrawal.provider.repository;

import com.wezaam.withdrawal.provider.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
