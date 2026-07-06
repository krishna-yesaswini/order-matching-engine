package com.kudirellilkrishnayesaswini.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrderWithCorrectFields() {
        Order order = new Order(1L, "RELIANCE", Order.Side.BUY, 250000L, 10L, System.currentTimeMillis());

        assertEquals(1L, order.orderId());
        assertEquals("RELIANCE", order.symbol());
        assertEquals(Order.Side.BUY, order.side());
        assertEquals(250000L, order.price());
        assertEquals(10L, order.quantity());
    }

    @Test
    void twoOrdersWithSameFieldsShouldBeEqual() {
        long timestamp = System.currentTimeMillis();
        Order order1 = new Order(1L, "RELIANCE", Order.Side.BUY, 250000L, 10L, timestamp);
        Order order2 = new Order(1L, "RELIANCE", Order.Side.BUY, 250000L, 10L, timestamp);

        assertEquals(order1, order2);
    }

    @Test
    void differentOrderIdsShouldNotBeEqual() {
        long timestamp = System.currentTimeMillis();
        Order order1 = new Order(1L, "RELIANCE", Order.Side.BUY, 250000L, 10L, timestamp);
        Order order2 = new Order(2L, "RELIANCE", Order.Side.BUY, 250000L, 10L, timestamp);

        assertNotEquals(order1, order2);
    }
}