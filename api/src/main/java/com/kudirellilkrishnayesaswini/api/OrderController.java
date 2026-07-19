package com.kudirellilkrishnayesaswini.api;

import com.kudirellilkrishnayesaswini.engine.OrderBook;
import com.kudirellilkrishnayesaswini.model.Order;
import com.kudirellilkrishnayesaswini.model.Trade;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderBook orderBook = new OrderBook();
    private final AtomicLong idGenerator = new AtomicLong(1);

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
        return orderBook.addOrder(order);
    }

    @GetMapping("/best-prices")
    public BestPrices getBestPrices() {
        return new BestPrices(orderBook.getBestBuyPrice(), orderBook.getBestSellPrice());
    }

    public record BestPrices(Long bestBuy, Long bestSell) {}
}