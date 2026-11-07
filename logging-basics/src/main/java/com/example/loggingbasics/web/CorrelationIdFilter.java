package com.example.loggingbasics.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * STUDENT TASK 3: give every request a correlation id, so all its log lines can be found together.
 * Choosing a safe id is already done in resolveCorrelationId.
 */
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {
    public static final String HEADER = "X-Correlation-Id";
    public static final String MDC_KEY = "correlationId";

    /** Accept only short, simple ids from clients, so nobody can inject text into our logs. */
    private static final Pattern SAFE_ID = Pattern.compile("[A-Za-z0-9-]{1,64}");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String correlationId = resolveCorrelationId(request.getHeader(HEADER));

        // TODO: put correlationId into MDC under MDC_KEY, so every log line of this request has it.
        // TODO: send it back to the client in the HEADER response header.
        try {
            MDC.put(MDC_KEY, correlationId);
            response.setHeader(HEADER, correlationId);
            chain.doFilter(request, response);
        } finally {
            // TODO: remove MDC_KEY from MDC. Server threads are reused, so a value left here
            // would appear in the logs of the next, unrelated request.
            MDC.remove(MDC_KEY);
        }
    }

    static String resolveCorrelationId(String header) {
        if (header != null && SAFE_ID.matcher(header).matches()) {
            return header;
        }
        return UUID.randomUUID().toString();
    }
}
