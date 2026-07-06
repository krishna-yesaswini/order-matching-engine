package com.kudirellilkrishnayesaswini.engine;

import com.kudirellilkrishnayesaswini.model.Order;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.TreeMap;

public class OrderBook {

    private final TreeMap<Long, Deque<Order>> buyOrders = new TreeMap<>();
    private final TreeMap<Long, Deque<Order>> sellOrders = new TreeMap<>();

    public void addOrder(Order order) {
        TreeMap<Long, Deque<Order>> book =
                order.side() == Order.Side.BUY ? buyOrders : sellOrders;

        book.computeIfAbsent(order.price(), key -> new ArrayDeque<>())
                .addLast(order);
    }

    public Long getBestBuyPrice() {
        if (buyOrders.isEmpty()) {
            return null;
        }
        return buyOrders.lastKey();
    }

    public Long getBestSellPrice() {
        if (sellOrders.isEmpty()) {
            return null;
        }
        return sellOrders.firstKey();
    }
}