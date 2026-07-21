package com.kudirellilkrishnayesaswini.benchmark;

import com.kudirellilkrishnayesaswini.engine.OrderBook;
import com.kudirellilkrishnayesaswini.model.Order;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class OrderBookBenchmark {

    private OrderBook orderBook;
    private AtomicLong idGenerator;

    @Setup(Level.Iteration)
    public void setup() {
        orderBook = new OrderBook();
        idGenerator = new AtomicLong(1);

        for (int i = 0; i < 100; i++) {
            orderBook.addOrder(new Order(
                    idGenerator.getAndIncrement(), "RELIANCE", Order.Side.SELL,
                    25000L + i, 10L, System.currentTimeMillis()
            ));
        }
    }

    @Benchmark
    public void addOrderThroughput() {
        Order order = new Order(
                idGenerator.getAndIncrement(), "RELIANCE", Order.Side.BUY,
                25050L, 5L, System.currentTimeMillis()
        );
        orderBook.addOrder(order);
    }
}