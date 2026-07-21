package com.kudirellilkrishnayesaswini.api.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;

@Component
public class GrpcServerRunner implements SmartLifecycle {

    private final OrderGrpcService orderGrpcService;
    private final int port;
    private Server server;

    public GrpcServerRunner(OrderGrpcService orderGrpcService, @Value("${grpc.server.port:9090}") int port) {
        this.orderGrpcService = orderGrpcService;
        this.port = port;
    }

    @Override
    public void start() {
        try {
            server = ServerBuilder.forPort(port)
                    .addService(orderGrpcService)
                    .build()
                    .start();
            System.out.println(">>> gRPC server started on port: " + port);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to start gRPC server", e);
        }
    }

    @Override
    @PreDestroy
    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    @Override
    public boolean isRunning() {
        return server != null && !server.isShutdown();
    }
}
