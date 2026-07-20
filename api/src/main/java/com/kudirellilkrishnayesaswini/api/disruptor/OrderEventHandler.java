package com.kudirellilkrishnayesaswini.api.disruptor;

import com.kudirellilkrishnayesaswini.engine.OrderBook;
import com.kudirellilkrishnayesaswini.model.Trade;
import com.lmax.disruptor.EventHandler;

import java.util.List;

public class OrderEventHandler implements EventHandler<OrderEvent> {

    private final OrderBook orderBook;

    public OrderEventHandler(OrderBook orderBook) {
        this.orderBook = orderBook;
    }

    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) {
        List<Trade> trades = orderBook.addOrder(event.getOrder());
        event.getResultFuture().complete(trades);
    }
}