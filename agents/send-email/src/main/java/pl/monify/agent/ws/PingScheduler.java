package pl.monify.agent.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PingScheduler {

    private final SessionRegistry registry;
    private final ObjectMapper mapper;

    public PingScheduler(SessionRegistry registry, ObjectMapper mapper) {
        this.registry = registry;
        this.mapper = mapper;
    }

    @Scheduled(fixedDelay = 300_000)
    public void ping() {
        if (registry.connected()) {
            try {
                var ping = Map.of("type", "ping");
                registry.get().send(mapper.writeValueAsString(ping));
            } catch (Exception ignored) {}
        }
    }
}
