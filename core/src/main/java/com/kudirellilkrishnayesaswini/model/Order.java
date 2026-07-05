package com.kudirellilkrishnayesaswini.model;

public record Order(
long orderId,
String symbol,
Side side,
long price,
long quantity,
long timestamp
) {
public enum Side {
    BUY,
    SELL
}
}

