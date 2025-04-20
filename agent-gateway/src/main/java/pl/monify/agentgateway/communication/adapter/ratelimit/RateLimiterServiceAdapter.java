package pl.monify.agentgateway.communication.adapter.ratelimit;

import pl.monify.agentgateway.communication.domain.port.out.ConnectionRateLimiterPort;
import pl.monify.agentgateway.communication.domain.port.out.MessageRateLimiterPort;

public class RateLimiterServiceAdapter implements ConnectionRateLimiterPort, MessageRateLimiterPort {

    private final AgentRateLimiter limiter;

    public RateLimiterServiceAdapter(AgentRateLimiter limiter) {
        this.limiter = limiter;
    }

    @Override
    public boolean allowConnection() {
        return limiter.allowConnection();
    }

    @Override
    public boolean isRateLimited(String agentId) {
        return limiter.allowMessage(agentId);
    }
}
