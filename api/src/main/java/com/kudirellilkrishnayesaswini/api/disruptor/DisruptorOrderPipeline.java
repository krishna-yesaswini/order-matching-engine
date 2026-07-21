package com.kudirellilkrishnayesaswini.api.disruptor;

import com.kudirellilkrishnayesaswini.engine.OrderBook;
import com.kudirellilkrishnayesaswini.model.Order;
import com.kudirellilkrishnayesaswini.model.Trade;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class DisruptorOrderPipeline {

    private final RingBuffer<OrderEvent> ringBuffer;
    private final OrderBook orderBook;

    public DisruptorOrderPipeline() {
        this.orderBook = new OrderBook();

        Disruptor<OrderEvent> disruptor = new Disruptor<>(
                new OrderEventFactory(),
                1024,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new BlockingWaitStrategy()
        );

        disruptor.handleEventsWith(new OrderEventHandler(orderBook));
        disruptor.start();

        this.ringBuffer = disruptor.getRingBuffer();
    }

    public List<Trade> submit(Order order) {
        long sequence = ringBuffer.next();
        CompletableFuture<List<Trade>> future = new CompletableFuture<>();

        try {
            OrderEvent event = ringBuffer.get(sequence);
            event.setOrder(order);
            event.setResultFuture(future);
        } finally {
            ringBuffer.publish(sequence);
        }

        try {
            return future.get(2, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException("Order processing failed or timed out", e);
        }
    }

    public Long getBestBuyPrice() {
        return orderBook.getBestBuyPrice();
    }

    public Long getBestSellPrice() {
        return orderBook.getBestSellPrice();
    }
}