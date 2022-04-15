package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.config.NotificationMQConfig;
import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalScheduled;
import com.wezaam.withdrawal.model.dto.WithdrawalDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

@Service
@RequiredArgsConstructor
public class EventsService {

    private final RabbitTemplate template;
    private final WithdrawalDTOFactory factory;

    private Queue<Object> queue = new LinkedList<>();

    public Queue<Object> getQueue() {
        return queue;
    }

    private void sendObject(Object item){
        if(item instanceof Withdrawal){
            this.send((Withdrawal) item);
        }
        else if(item instanceof WithdrawalScheduled){
            this.send((WithdrawalScheduled) item);
        }
    }
    @Async
    public void send(Withdrawal withdrawal) {
        // build and send an event in message queue async
        try {
            if (withdrawal != null) {
                WithdrawalDTO dto = factory.createByWithdrawal(withdrawal);
                template.convertAndSend(NotificationMQConfig.EXCHANGE, NotificationMQConfig.ROUTING_KEY, dto);
            }
        } catch (Exception e) {
            addToQueue(withdrawal);
        }
    }

    private void addToQueue(Object withdrawal) {
        synchronized (queue) {
            queue.add(withdrawal);
        }
    }

    @Async
    public void send(WithdrawalScheduled withdrawal) {
        // build and send an event in message queue async
        try {
            if (withdrawal != null) {
                WithdrawalDTO dto = factory.createByWithdrawalSchedule(withdrawal);
                template.convertAndSend(NotificationMQConfig.EXCHANGE, NotificationMQConfig.ROUTING_KEY, dto);
            }
        } catch (Exception e) {
            addToQueue(withdrawal);
        }
    }

    @Scheduled(fixedDelay = 10000)
    public void run() {
        var oldItems = new ArrayList();

        synchronized (queue) {
            while(!queue.isEmpty()) {
                oldItems.add(queue.poll());
            }
        }
        oldItems.stream().forEach(it -> sendObject(it));

    }
}
