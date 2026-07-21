package com.kudirellilkrishnayesaswini.api;

import com.kudirellilkrishnayesaswini.api.disruptor.DisruptorOrderPipeline;
import com.kudirellilkrishnayesaswini.model.Order;
import com.kudirellilkrishnayesaswini.model.Trade;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final DisruptorOrderPipeline pipeline;
    private final AtomicLong idGenerator = new AtomicLong(1);

    public OrderController(DisruptorOrderPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public record OrderRequest(String symbol, String side, long price, long quantity) {}

    @PostMapping
    public List<Trade> submitOrder(@RequestBody OrderRequest request) {
        Order order = new Order(
                idGenerator.getAndIncrement(),
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