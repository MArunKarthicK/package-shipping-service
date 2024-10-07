package com.abnamro.packageshippingservice.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class CustomLoggingFilter implements Filter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String correlationId = Optional.ofNullable(httpRequest.getHeader(CORRELATION_ID_HEADER))
                .orElseGet(() -> UUID.randomUUID().toString());

        String requestId = Optional.ofNullable(httpRequest.getHeader(REQUEST_ID_HEADER))
                .orElseGet(() -> UUID.randomUUID().toString());

        MDC.put(CORRELATION_ID_HEADER, correlationId);
        MDC.put(REQUEST_ID_HEADER, requestId);

        try {
            log.info("Incoming Request: Method: {} | URI: {} |", httpRequest.getMethod(), httpRequest.getRequestURI());
            httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);
            httpResponse.setHeader(REQUEST_ID_HEADER, requestId);
            chain.doFilter(request, response);
            log.info("Outgoing Response: Status: {}", httpResponse.getStatus());
        } catch (Exception e) {
            log.error("Error occurred in filter:{}", e.getMessage());
        } finally {
            MDC.clear();
        }
    }
}

