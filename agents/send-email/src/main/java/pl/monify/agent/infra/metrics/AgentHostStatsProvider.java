package pl.monify.agent.infra.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AgentHostStatsProvider {

    private final MeterRegistry meterRegistry;
    private final Environment environment;

    public AgentHostStatsProvider(MeterRegistry meterRegistry, Environment environment) {
        this.meterRegistry = meterRegistry;
        this.environment = environment;
    }

    public AgentHostStats getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        getMetric("system.cpu.usage").ifPresent(v -> stats.put("cpu.usage", v));
        getMetric("system.cpu.count").ifPresent(v -> stats.put("cpu.count", v));
        getMetric("system.load.average.1m").ifPresent(v -> stats.put("load.1m", v));
        getMetric("system.memory.total").ifPresent(v -> stats.put("memory.total", v));
        getMetric("system.memory.used").ifPresent(v -> stats.put("memory.used", v));
        getMetric("jvm.memory.used").ifPresent(v -> stats.put("jvm.memory.used", v));
        getMetric("jvm.threads.live").ifPresent(v -> stats.put("jvm.threads.live", v));
        getMetric("process.uptime").ifPresent(v -> stats.put("uptime", v));

        stats.put("spring.profiles.active", String.join(",", environment.getActiveProfiles()));
        stats.put("java.version", System.getProperty("java.version"));
        stats.put("os.name", System.getProperty("os.name"));
        stats.put("os.arch", System.getProperty("os.arch"));

        return new AgentHostStats(stats);
    }

    private java.util.Optional<Double> getMetric(String name) {
        try {
            Gauge gauge = meterRegistry.find(name).gauge();

            return gauge != null
                    ? java.util.Optional.of(gauge.value())
                    : java.util.Optional.empty();
        } catch (Exception e) {
            return java.util.Optional.empty();
        }
    }
}
