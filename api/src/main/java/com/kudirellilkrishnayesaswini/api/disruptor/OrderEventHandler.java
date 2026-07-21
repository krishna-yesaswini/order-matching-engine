package com.kudirellilkrishnayesaswini.api.disruptor;

import com.kudirellilkrishnayesaswini.engine.OrderBook;
import com.kudirellilkrishnayesaswini.engine.TradeStore;
import com.kudirellilkrishnayesaswini.model.Trade;
import com.lmax.disruptor.EventHandler;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class OrderEventHandler implements EventHandler<OrderEvent> {

    private final OrderBook orderBook;
    private final TradeStore tradeStore;
    private final AtomicLong tradeSequence = new AtomicLong(1);

    public OrderEventHandler(OrderBook orderBook, TradeStore tradeStore) {
        this.orderBook = orderBook;
        this.tradeStore = tradeStore;
    }

    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) {
        List<Trade> trades = orderBook.addOrder(event.getOrder());
        for (Trade trade : trades) {
            tradeStore.save(tradeSequence.getAndIncrement(), trade);
        }
        event.getResultFuture().complete(trades);
    }
}