package com.kudirellilkrishnayesaswini.api.grpc;

import com.kudirellilkrishnayesaswini.api.disruptor.DisruptorOrderPipeline;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {

    private final DisruptorOrderPipeline pipeline;

    public OrderGrpcService(DisruptorOrderPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public void submitOrder(SubmitOrderRequest request, StreamObserver<SubmitOrderResponse> responseObserver) {
        com.kudirellilkrishnayesaswini.model.Order.Side side = request.getSide() == Order.Side.SELL
                ? com.kudirellilkrishnayesaswini.model.Order.Side.SELL
                : com.kudirellilkrishnayesaswini.model.Order.Side.BUY;

        com.kudirellilkrishnayesaswini.model.Order order = new com.kudirellilkrishnayesaswini.model.Order(
                pipeline.nextOrderId(),
                request.getSymbol(),
                side,
                request.getPrice(),
                request.getQuantity(),
                System.currentTimeMillis()
        );

        List<com.kudirellilkrishnayesaswini.model.Trade> trades = pipeline.submit(order);

        SubmitOrderResponse.Builder response = SubmitOrderResponse.newBuilder();
        for (com.kudirellilkrishnayesaswini.model.Trade trade : trades) {
            response.addTrades(Trade.newBuilder()
                    .setBuyOrderId(trade.buyOrderId())
                    .setSellOrderId(trade.sellOrderId())
                    .setSymbol(trade.symbol())
                    .setPrice(trade.price())
                    .setQuantity(trade.quantity())
                    .setTimestamp(trade.timestamp())
                    .build());
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
