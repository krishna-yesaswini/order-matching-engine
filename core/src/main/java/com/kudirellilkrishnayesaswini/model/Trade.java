package com.kudirellilkrishnayesaswini.model;

public record Trade(
    long buyOrderId,
    long sellOrderId,
    String symbol,
    long price,
    long quantity,
    long timestamp
){

}
