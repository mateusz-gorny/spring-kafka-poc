package pl.monify.agentgateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for circuit breakers in the application.
 * This allows for configurable circuit breaker settings for different components.
 */
@Configuration
@EnableConfigurationProperties(CircuitBreakerConfig.CircuitBreakerProperties.class)
public class CircuitBreakerConfig {

    /**
     * Configuration properties for circuit breakers.
     */
    @ConfigurationProperties(prefix = "monify.circuit-breaker")
    public record CircuitBreakerProperties(
            Map<String, CircuitSettings> circuits,
            CircuitSettings defaults
    ) {
        /**
         * Constructor with default values.
         */
        public CircuitBreakerProperties {
            if (defaults == null) {
                defaults = new CircuitSettings(5, 10, 5);
            }
            
            if (circuits == null) {
                circuits = new HashMap<>();
            }
            
            // Ensure we have default settings for the Kafka producer circuit
            if (!circuits.containsKey("kafka-producer")) {
                circuits = new HashMap<>(circuits);
                circuits.put("kafka-producer", new CircuitSettings(5, 10, 5));
            }
        }
        
        /**
         * Get settings for a specific circuit, falling back to defaults if not found.
         *
         * @param circuitName the name of the circuit
         * @return the circuit settings
         */
        public CircuitSettings getSettingsForCircuit(String circuitName) {
            return circuits.getOrDefault(circuitName, defaults);
        }
    }
    
    /**
     * Settings for a specific circuit breaker.
     */
    public record CircuitSettings(
            int failureThreshold,
            int resetTimeoutSeconds,
            int timeoutSeconds
    ) {
        /**
         * Constructor with validation.
         */
        public CircuitSettings {
            if (failureThreshold <= 0) {
                failureThreshold = 5;
            }
            if (resetTimeoutSeconds <= 0) {
                resetTimeoutSeconds = 10;
            }
            if (timeoutSeconds <= 0) {
                timeoutSeconds = 5;
            }
        }
        
        /**
         * Get the reset timeout as a Duration.
         *
         * @return the reset timeout
         */
        public Duration getResetTimeout() {
            return Duration.ofSeconds(resetTimeoutSeconds);
        }
    }
    
    /**
     * Bean for accessing circuit breaker settings.
     *
     * @param properties the circuit breaker properties
     * @return a bean for accessing circuit settings
     */
    @Bean
    public CircuitBreakerSettingsProvider circuitBreakerSettingsProvider(CircuitBreakerProperties properties) {
        return new CircuitBreakerSettingsProvider(properties);
    }
    
    /**
     * Provider for circuit breaker settings.
     */
    public static class CircuitBreakerSettingsProvider {
        private final CircuitBreakerProperties properties;
        
        public CircuitBreakerSettingsProvider(CircuitBreakerProperties properties) {
            this.properties = properties;
        }
        
        /**
         * Get the failure threshold for a circuit.
         *
         * @param circuitName the name of the circuit
         * @return the failure threshold
         */
        public int getFailureThreshold(String circuitName) {
            return properties.getSettingsForCircuit(circuitName).failureThreshold();
        }
        
        /**
         * Get the reset timeout for a circuit.
         *
         * @param circuitName the name of the circuit
         * @return the reset timeout
         */
        public Duration getResetTimeout(String circuitName) {
            return properties.getSettingsForCircuit(circuitName).getResetTimeout();
        }
        
        /**
         * Get the timeout in seconds for a circuit operation.
         *
         * @param circuitName the name of the circuit
         * @return the timeout in seconds
         */
        public int getTimeoutSeconds(String circuitName) {
            return properties.getSettingsForCircuit(circuitName).timeoutSeconds();
        }
    }
}