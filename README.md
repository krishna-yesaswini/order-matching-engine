# High-Performance Order Matching Engine

A Java 21 order matching engine modeled on real exchange architecture — price-time priority matching, a lock-free intake pipeline built on the LMAX Disruptor, and a live REST API with a working demo.

**Live demo:** https://order-matching-engine-6dfs.onrender.com/index.html
*(Free-tier hosting — the first request after a period of inactivity may take 30–60s to wake up.)*

---

## What it does

Submits BUY/SELL orders for a symbol, matches them against a resting order book using price-time priority (best price first, oldest order at that price first), and returns any resulting trades. Unmatched quantity rests in the book waiting for a future match.

## Architecture
**Why split this way:** `core` has no framework dependencies, so the matching logic can be tested in milliseconds without spinning up a server. `api` wraps it for network access. `benchmark` measures it in isolation, separate from correctness tests.

## Core design decisions

- **`Order` / `Trade` are immutable Java records.** An order is an intent that may partially fill; a trade is a completed, permanent fact. Keeping them immutable avoids a class of concurrency bugs where one thread reads state while another mutates it.
- **Price is stored as a `long` (smallest currency unit), never a `double`.** Floating-point rounding errors are unacceptable when the numbers represent money.
- **`OrderBook` uses two `TreeMap<Long, Deque<Order>>`** — one for bids, one for asks. `TreeMap` keeps prices sorted via a Red-Black Tree, giving O(log n) access to the best price on each side (`lastKey()` for the highest bid, `firstKey()` for the lowest ask). Each price level holds an `ArrayDeque` for O(1) FIFO ordering — first order in at a price is the first one matched.
- **Order intake runs through an LMAX Disruptor ring buffer**, funneling all incoming orders through a single consumer thread. This avoids the need for locks around `OrderBook`'s mutable state entirely, rather than adding synchronization after the fact.
- **Trades persist off-heap via Chronicle Map**, a memory-mapped key-value store — survives restarts without adding garbage-collector pressure from normal heap allocation.
- **gRPC/Protobuf runs alongside the REST API**, both routed through the same Disruptor pipeline — REST for easy integration, gRPC for lower-overhead binary communication between services.

## Performance

Benchmarked with JMH (industry-standard JVM microbenchmarking, built by the JVM team) on the core matching path in isolation:
This measures in-memory matching only — no network or I/O overhead included.

## Tech stack

Java 21 · Spring Boot 3.3 · LMAX Disruptor · Chronicle Map · gRPC/Protobuf · JMH · Maven (multi-module) · Docker · deployed on Render

## Running locally

```bash
mvn clean install
cd api
mvn spring-boot:run
```

Then open `http://localhost:8080/index.html`.

## Tests

```bash
mvn test
```

7 passing JUnit 5 tests covering `Order` equality/field correctness and `OrderBook` price-priority and matching behavior.

## Known issues

- Order-of-submission dependent matching behavior observed in the deployed environment (SELL-then-BUY sequence not always producing an expected match) — not yet root-caused; local testing during development showed correct matching in this sequence. Flagging for follow-up investigation.

## Roadmap

- [ ] ZGC tuning for low-pause GC under load