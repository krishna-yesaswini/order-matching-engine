package com.kudirellilkrishnayesaswini.api;

import com.kudirellilkrishnayesaswini.api.disruptor.DisruptorOrderPipeline;
import com.kudirellilkrishnayesaswini.model.Order;
import com.kudirellilkrishnayesaswini.model.Trade;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final DisruptorOrderPipeline pipeline;

    public OrderController(DisruptorOrderPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public record OrderRequest(String symbol, String side, long price, long quantity) {}

    @PostMapping
    public List<Trade> submitOrder(@RequestBody OrderRequest request) {
        Order order = new Order(
                pipeline.nextOrderId(),
                request.symbol(),
                Order.Side.valueOf(request.side().toUpperCase()),
                request.price(),
                request.quantity(),
                System.currentTimeMillis()
        );
        return pipeline.submit(order);
    }

    @GetMapping("/best-prices")
    public BestPrices getBestPrices() {
        return new BestPrices(pipeline.getBestBuyPrice(), pipeline.getBestSellPrice());
    }

    public record BestPrices(Long bestBuy, Long bestSell) {}
}