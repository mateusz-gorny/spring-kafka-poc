package pl.monify.agentgateway.communication.domain.model;

import java.util.Collections;
import java.util.Map;

public record AgentHostStats(Map<String, Object> metrics) {

    public AgentHostStats {
        metrics = metrics == null ? Collections.emptyMap() : Map.copyOf(metrics);
    }

    public Map<String, Object> asMap() {
        return metrics;
    }
}
