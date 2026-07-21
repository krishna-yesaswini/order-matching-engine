package com.kudirellilkrishnayesaswini.model;

import java.io.Serializable;

public record Trade(
        long buyOrderId,
        long sellOrderId,
        String symbol,
        long price,
        long quantity,
        long timestamp
) implements Serializable {
}