package com.shadowledger.api_gateway.filter;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class TraceIdGlobalFilter implements GlobalFilter {

    private static final String TRACE_HEADER = "X-Trace-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String traceId = exchange.getRequest()
                .getHeaders()
                .getFirst(TRACE_HEADER);

        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }

        MDC.put("traceId", traceId);

        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header(TRACE_HEADER, traceId)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .doFinally(signal -> MDC.clear());
    }
}
