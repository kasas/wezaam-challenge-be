package com.wezaam.withdrawal.provider;

import com.wezaam.withdrawal.provider.model.TransactionStatus;
import com.wezaam.withdrawal.provider.repository.TransactionRepository;
import com.wezaam.withdrawal.provider.service.TransactionEventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableAsync
class PaymentProviderImplIntegrationTest {
    @Autowired
    private TransactionRepository repository;
    @Autowired
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private TransactionEventService eventService;

    private CountDownLatch lock = new CountDownLatch(1);
    @Test
    public void itShouldCreateATransaction(){
        //given empty repository
        PaymentProviderImpl provider = new PaymentProviderImpl(repository,eventService);

        //when
        var transaction = provider.create(1L, 100D);

        //then
        assertNotNull(transaction.orElse(null));
        assertNotNull(transaction.get().getId());
        assertTrue(transaction.get().getId()>0);

    }

    @Test
    public void itShouldProcessTransactionAsync() throws InterruptedException {
        //given
        PaymentProviderImpl provider = new PaymentProviderImpl(repository, eventService);
        var transaction = provider.create(1L, 100D);

        //when
        provider.process(transaction.get());

        //then
        try {
            lock.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
        transaction = repository.findById(transaction.get().getId());
        assertEquals(TransactionStatus.SUCCESS, transaction.get().getStatus());


    }

}