package com.kudirellilkrishnayesaswini.engine;

import com.kudirellilkrishnayesaswini.model.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderBookTest {

    @Test
    void newOrderBookShouldHaveNoBestPrices() {
        OrderBook book = new OrderBook();

        assertNull(book.getBestBuyPrice());
        assertNull(book.getBestSellPrice());
    }

    @Test
    void bestBuyPriceShouldBeHighestBuyOrder() {
        OrderBook book = new OrderBook();

        book.addOrder(new Order(1L, "RELIANCE", Order.Side.BUY, 24000L, 10L, 1L));
        book.addOrder(new Order(2L, "RELIANCE", Order.Side.BUY, 25000L, 5L, 2L));
        book.addOrder(new Order(3L, "RELIANCE", Order.Side.BUY, 23000L, 8L, 3L));

        assertEquals(25000L, book.getBestBuyPrice());
    }

    @Test
    void bestSellPriceShouldBeLowestSellOrder() {
        OrderBook book = new OrderBook();

        book.addOrder(new Order(1L, "RELIANCE", Order.Side.SELL, 26000L, 10L, 1L));
        book.addOrder(new Order(2L, "RELIANCE", Order.Side.SELL, 25500L, 5L, 2L));
        book.addOrder(new Order(3L, "RELIANCE", Order.Side.SELL, 27000L, 8L, 3L));

        assertEquals(25500L, book.getBestSellPrice());
    }
}