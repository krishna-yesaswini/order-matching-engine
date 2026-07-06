package com.kudirellilkrishnayesaswini.engine;

import com.kudirellilkrishnayesaswini.model.Order;
import com.kudirellilkrishnayesaswini.model.Trade;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.TreeMap;

public class OrderBook {

    private final TreeMap<Long, Deque<Order>> buyOrders = new TreeMap<>();
    private final TreeMap<Long, Deque<Order>> sellOrders = new TreeMap<>();

    public List<Trade> addOrder(Order incomingOrder) {
        List<Trade> trades = new ArrayList<>();

        if (incomingOrder.side() == Order.Side.BUY) {
            matchBuyOrder(incomingOrder, trades);
        } else {
            matchSellOrder(incomingOrder, trades);
        }

        return trades;
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

    private void matchBuyOrder(Order incomingBuy, List<Trade> trades) {
        long remainingQuantity = incomingBuy.quantity();

        while (remainingQuantity > 0 && !sellOrders.isEmpty()
                && sellOrders.firstKey() <= incomingBuy.price()) {

            Long bestSellPrice = sellOrders.firstKey();
            Deque<Order> sellQueue = sellOrders.get(bestSellPrice);
            Order restingSell = sellQueue.peekFirst();

            long tradedQuantity = Math.min(remainingQuantity, restingSell.quantity());

            trades.add(new Trade(
                    incomingBuy.orderId(),
                    restingSell.orderId(),
                    incomingBuy.symbol(),
                    restingSell.price(),
                    tradedQuantity,
                    System.currentTimeMillis()
            ));

            remainingQuantity -= tradedQuantity;

            if (tradedQuantity == restingSell.quantity()) {
                sellQueue.pollFirst();
                if (sellQueue.isEmpty()) {
                    sellOrders.remove(bestSellPrice);
                }
            } else {
                Order updatedSell = new Order(
                        restingSell.orderId(), restingSell.symbol(), restingSell.side(),
                        restingSell.price(), restingSell.quantity() - tradedQuantity,
                        restingSell.timestamp()
                );
                sellQueue.pollFirst();
                sellQueue.addFirst(updatedSell);
            }
        }

        if (remainingQuantity > 0) {
            Order restingBuy = new Order(
                    incomingBuy.orderId(), incomingBuy.symbol(), incomingBuy.side(),
                    incomingBuy.price(), remainingQuantity, incomingBuy.timestamp()
            );
            buyOrders.computeIfAbsent(restingBuy.price(), k -> new ArrayDeque<>())
                    .addLast(restingBuy);
        }
    }

    private void matchSellOrder(Order incomingSell, List<Trade> trades) {
        long remainingQuantity = incomingSell.quantity();

        while (remainingQuantity > 0 && !buyOrders.isEmpty()
                && buyOrders.lastKey() >= incomingSell.price()) {

            Long bestBuyPrice = buyOrders.lastKey();
            Deque<Order> buyQueue = buyOrders.get(bestBuyPrice);
            Order restingBuy = buyQueue.peekFirst();

            long tradedQuantity = Math.min(remainingQuantity, restingBuy.quantity());

            trades.add(new Trade(
                    restingBuy.orderId(),
                    incomingSell.orderId(),
                    incomingSell.symbol(),
                    restingBuy.price(),
                    tradedQuantity,
                    System.currentTimeMillis()
            ));

            remainingQuantity -= tradedQuantity;

            if (tradedQuantity == restingBuy.quantity()) {
                buyQueue.pollFirst();
                if (buyQueue.isEmpty()) {
                    buyOrders.remove(bestBuyPrice);
                }
            } else {
                Order updatedBuy = new Order(
                        restingBuy.orderId(), restingBuy.symbol(), restingBuy.side(),
                        restingBuy.price(), restingBuy.quantity() - tradedQuantity,
                        restingBuy.timestamp()
                );
                buyQueue.pollFirst();
                buyQueue.addFirst(updatedBuy);
            }
        }

        if (remainingQuantity > 0) {
            Order restingSell = new Order(
                    incomingSell.orderId(), incomingSell.symbol(), incomingSell.side(),
                    incomingSell.price(), remainingQuantity, incomingSell.timestamp()
            );
            sellOrders.computeIfAbsent(restingSell.price(), k -> new ArrayDeque<>())
                    .addLast(restingSell);
        }
    }
}