package com.kudirellilkrishnayesaswini.api.disruptor;

import com.kudirellilkrishnayesaswini.model.Order;
import com.kudirellilkrishnayesaswini.model.Trade;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OrderEvent {
    private Order order;
    private CompletableFuture<List<Trade>> resultFuture;

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public CompletableFuture<List<Trade>> getResultFuture() { return resultFuture; }
    public void setResultFuture(CompletableFuture<List<Trade>> resultFuture) { this.resultFuture = resultFuture; }
}