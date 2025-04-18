package pl.monify.agentgateway.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.monify.agentgateway.config.WebSocketProperties;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for rate limiting WebSocket connections and messages.
 */
@Service
public class RateLimiterService {
    private static final Logger log = LoggerFactory.getLogger(RateLimiterService.class);

    private final WebSocketProperties properties;
    private final AtomicInteger connectionCounter = new AtomicInteger(0);
    private final Map<String, AtomicInteger> messageCounters = new ConcurrentHashMap<>();
    private long lastConnectionResetTime = System.currentTimeMillis();
    private long lastMessageResetTime = System.currentTimeMillis();

    public RateLimiterService(WebSocketProperties properties) {
        this.properties = properties;
    }

    /**
     * Check if a new connection is allowed based on rate limits.
     *
     * @return true if the connection is allowed, false otherwise
     */
    public synchronized boolean allowConnection() {
        long now = System.currentTimeMillis();
        if (now - lastConnectionResetTime > 1000) {
            // Reset counter every second
            connectionCounter.set(0);
            lastConnectionResetTime = now;
        }

        int currentCount = connectionCounter.incrementAndGet();
        boolean allowed = currentCount <= properties.rateLimiting().connectionsPerSecond();
        
        if (!allowed) {
            log.warn("Connection rate limit exceeded: {} connections/second", 
                    properties.rateLimiting().connectionsPerSecond());
            connectionCounter.decrementAndGet(); // Decrement since we're rejecting
        }
        
        return allowed;
    }

    /**
     * Check if a new message from an agent is allowed based on rate limits.
     *
     * @param agentId the ID of the agent sending the message
     * @return true if the message is allowed, false otherwise
     */
    public synchronized boolean allowMessage(String agentId) {
        long now = System.currentTimeMillis();
        if (now - lastMessageResetTime > 1000) {
            // Reset all message counters every second
            messageCounters.clear();
            lastMessageResetTime = now;
        }

        AtomicInteger counter = messageCounters.computeIfAbsent(agentId, k -> new AtomicInteger(0));
        int currentCount = counter.incrementAndGet();
        boolean allowed = currentCount <= properties.rateLimiting().messagesPerSecond();
        
        if (!allowed) {
            log.warn("Message rate limit exceeded for agent {}: {} messages/second", 
                    agentId, properties.rateLimiting().messagesPerSecond());
            counter.decrementAndGet(); // Decrement since we're rejecting
        }
        
        return allowed;
    }
}