package pl.monify.agentgateway.agentdelivery.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.monify.agentgateway.config.CircuitBreakerConfig.CircuitBreakerSettingsProvider;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple circuit breaker implementation for Kafka operations.
 */
@Service
public class CircuitBreakerService {
    private static final Logger log = LoggerFactory.getLogger(CircuitBreakerService.class);

    private final CircuitBreakerSettingsProvider settings;
    private final Map<String, CircuitState> circuits = new ConcurrentHashMap<>();

    public CircuitBreakerService(CircuitBreakerSettingsProvider settings) {
        this.settings = settings;
    }

    private class CircuitState {
        private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
        private final AtomicInteger failureCount = new AtomicInteger(0);
        private volatile Instant lastFailure;
        private final String circuitName;

        enum State {
            CLOSED, OPEN, HALF_OPEN
        }

        CircuitState(String circuitName) {
            this.circuitName = circuitName;
        }

        boolean isAllowed() {
            State currentState = state.get();
            if (currentState == State.CLOSED) {
                return true;
            } else if (currentState == State.OPEN) {
                Duration resetTimeout = settings.getResetTimeout(circuitName);
                if (Duration.between(lastFailure, Instant.now()).compareTo(resetTimeout) > 0) {
                    // Try to transition to HALF_OPEN
                    if (state.compareAndSet(State.OPEN, State.HALF_OPEN)) {
                        return true;
                    }
                }
                return false;
            } else { // HALF_OPEN
                return true;
            }
        }

        void recordSuccess() {
            if (state.get() == State.HALF_OPEN) {
                state.set(State.CLOSED);
                failureCount.set(0);
            } else if (state.get() == State.CLOSED) {
                failureCount.set(0);
            }
        }

        void recordFailure() {
            lastFailure = Instant.now();
            if (state.get() == State.HALF_OPEN) {
                state.set(State.OPEN);
            } else if (state.get() == State.CLOSED) {
                int threshold = settings.getFailureThreshold(circuitName);
                if (failureCount.incrementAndGet() >= threshold) {
                    state.set(State.OPEN);
                }
            }
        }

        State getState() {
            return state.get();
        }
    }

    /**
     * Checks if the operation is allowed for the given circuit name.
     *
     * @param circuitName the name of the circuit
     * @return true if the operation is allowed, false otherwise
     */
    public boolean isAllowed(String circuitName) {
        CircuitState circuit = circuits.computeIfAbsent(circuitName, k -> new CircuitState(k));
        boolean allowed = circuit.isAllowed();
        if (!allowed) {
            log.warn("Circuit '{}' is OPEN, operation not allowed", circuitName);
        }
        return allowed;
    }

    /**
     * Records a successful operation for the given circuit name.
     *
     * @param circuitName the name of the circuit
     */
    public void recordSuccess(String circuitName) {
        CircuitState circuit = circuits.get(circuitName);
        if (circuit != null) {
            circuit.recordSuccess();
            if (circuit.getState() == CircuitState.State.CLOSED) {
                log.info("Circuit '{}' is now CLOSED", circuitName);
            }
        }
    }

    /**
     * Records a failed operation for the given circuit name.
     *
     * @param circuitName the name of the circuit
     */
    public void recordFailure(String circuitName) {
        CircuitState circuit = circuits.computeIfAbsent(circuitName, k -> new CircuitState(k));
        CircuitState.State oldState = circuit.getState();
        circuit.recordFailure();
        CircuitState.State newState = circuit.getState();

        if (oldState != newState && newState == CircuitState.State.OPEN) {
            log.warn("Circuit '{}' is now OPEN due to failures", circuitName);
        }
    }
}
