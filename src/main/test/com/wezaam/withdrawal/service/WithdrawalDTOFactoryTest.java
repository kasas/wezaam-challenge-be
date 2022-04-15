package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalScheduled;
import com.wezaam.withdrawal.model.dto.WithdrawalDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WithdrawalDTOFactoryTest {

    @Test
    void itShouldCreateWithdrawalDTO() {
        //give
        Withdrawal item = Withdrawal.builder().id(1L).build();
        WithdrawalDTOFactory factory = new WithdrawalDTOFactory();
        //when

        WithdrawalDTO dto = factory.createByWithdrawal(item);

        //then
        assertNotNull(dto);
        assertEquals(1L, dto.getId(), "It is same Id");
    }

    @Test
    void itShouldCreateWithdrawalScheduledDTO() {
        //give
        WithdrawalScheduled item = WithdrawalScheduled.builder().id(1L).build();
        WithdrawalDTOFactory factory = new WithdrawalDTOFactory();
        //when

        WithdrawalDTO dto = factory.createByWithdrawalSchedule(item);

        //then
        assertNotNull(dto);
        assertEquals(1L, dto.getId(), "It is same Id");
    }
    @Test
    void itShouldCreateDTO() {
        //give
        WithdrawalDTOFactory factory = new WithdrawalDTOFactory();
        //when

        WithdrawalDTO dto = factory.createByWithdrawalSchedule(null);

        //then
        assertNotNull(dto);
    }
}