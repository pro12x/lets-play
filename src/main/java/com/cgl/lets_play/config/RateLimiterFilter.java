package com.cgl.lets_play.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterFilter extends OncePerRequestFilter
{
    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${rate-limit.enabled:true}")
    private boolean enabled;
    @Value("${rate-limit.request-per-second:10}")
    private int requestPerSecond;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);

        TokenBucket tokenBucket = buckets.computeIfAbsent(clientIp, k -> new TokenBucket(requestPerSecond));

        if (tokenBucket.tryConsume()) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            Map<String, Object> errorDetails = Map.of(
                    "status", HttpStatus.TOO_MANY_REQUESTS.value(),
                    "error", "Too Many Requests",
                    "message", "Rate limit exceeded. Try again later."
            );

            response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private static class TokenBucket {
        private final int refillRate;
        private double tokens;
        private long lastRefillTimestamp;

        public TokenBucket(int refillRate) {
            this.refillRate = refillRate;
            this.tokens = refillRate;
            this.lastRefillTimestamp = System.currentTimeMillis();
        }

        public synchronized boolean tryConsume() {
            refill();

            if (tokens >= 1) {
                tokens--;
                return true;
            }

            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            double tokensToAdd = (now - lastRefillTimestamp) / 1000.0 * refillRate;

            if (tokensToAdd > 0) {
                tokens = Math.min(refillRate, tokens + tokensToAdd);
                lastRefillTimestamp = now;
            }
        }
    }
}
