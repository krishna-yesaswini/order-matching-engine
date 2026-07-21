package com.kudirellilkrishnayesaswini.engine;

import com.kudirellilkrishnayesaswini.model.Trade;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;

import java.io.File;
import java.io.IOException;

public class TradeStore implements AutoCloseable {

    private final ChronicleMap<Long, Trade> trades;

    public TradeStore(String filePath) throws IOException {
        File file = new File(filePath).getAbsoluteFile();
        System.out.println(">>> TradeStore writing to: " + file.getAbsolutePath());

        this.trades = ChronicleMapBuilder.of(Long.class, Trade.class)
                .name("trade-store")
                .entries(1_000_000)
                .averageValueSize(128)
                .createPersistedTo(file);
    }

    public void save(long tradeSequenceId, Trade trade) {
        trades.put(tradeSequenceId, trade);
    }

    public Trade get(long tradeSequenceId) {
        return trades.get(tradeSequenceId);
    }

    public long size() {
        return trades.size();
    }

    @Override
    public void close() {
        trades.close();
    }
}