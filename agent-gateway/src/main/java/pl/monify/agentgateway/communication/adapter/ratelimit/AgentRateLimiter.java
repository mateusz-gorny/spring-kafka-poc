package pl.monify.agentgateway.communication.adapter.ratelimit;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AgentRateLimiter {

    private final int maxConnections;
    private final int maxMessagesPerSecond;

    private int currentConnections = 0;
    private final Map<String, AgentLimit> agentLimits = new ConcurrentHashMap<>();

    public AgentRateLimiter(int maxConnections, int maxMessagesPerSecond) {
        this.maxConnections = maxConnections;
        this.maxMessagesPerSecond = maxMessagesPerSecond;
    }

    public synchronized boolean allowConnection() {
        if (currentConnections >= maxConnections) {
            return false;
        }
        currentConnections++;
        return true;
    }

    public boolean allowMessage(String agentId) {
        AgentLimit limit = agentLimits.computeIfAbsent(agentId, key -> new AgentLimit(maxMessagesPerSecond));
        return limit.tryConsume();
    }

    private static class AgentLimit {
        private final int maxPerSecond;
        private int used = 0;
        private Instant window = Instant.now();

        AgentLimit(int maxPerSecond) {
            this.maxPerSecond = maxPerSecond;
        }

        synchronized boolean tryConsume() {
            Instant now = Instant.now();
            if (now.isAfter(window.plusSeconds(1))) {
                used = 0;
                window = now;
            }
            if (used < maxPerSecond) {
                used++;
                return true;
            }
            return false;
        }
    }
}
