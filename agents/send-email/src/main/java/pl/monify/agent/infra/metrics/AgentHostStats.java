package pl.monify.agent.infra.metrics;

import java.util.Collections;
import java.util.Map;

public class AgentHostStats {

    private final Map<String, Object> metrics;

    public AgentHostStats(Map<String, Object> metrics) {
        this.metrics = metrics == null ? Collections.emptyMap() : Map.copyOf(metrics);
    }

    public Map<String, Object> asMap() {
        return metrics;
    }
}
