package pl.monify.agentgateway.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Initializes MongoDB with sample data for testing.
 * This replaces the schema.sql file used with H2.
 */
@Configuration
public class MongoDbInitializer {

    private static final Logger logger = LoggerFactory.getLogger(MongoDbInitializer.class);

    /**
     * Initializes the MongoDB database with sample data.
     *
     * @param agentRepository The agent repository.
     * @return A CommandLineRunner that initializes the database.
     */
//    @Bean
//    @PostConstruct
//    public CommandLineRunner initDatabase(AgentRepository agentRepository) {
//        return args -> {
//            logger.info("Initializing MongoDB database with sample data");
//
//            // Check if agents collection is empty
//            agentRepository.count()
//                .flatMap(count -> {
//                    if (count == 0) {
//                        logger.info("Agents collection is empty, initializing with sample data");
//
//                        // Create sample agents
//                        List<Agent> agents = Arrays.asList(
//                                new Agent("agent1", "team1", "secret1", Instant.now(), null),
//                                new Agent("agent2", "team1", "secret2", Instant.now(), null),
//                                new Agent("agent3", "team2", "secret3", Instant.now(), null)
//                        );
//
//                        // Save agents
//                        return agentRepository.saveAll(agents).collectList()
//                                .doOnSuccess(savedAgents ->
//                                    logger.info("Sample agents created: {}", savedAgents.size())
//                                );
//                    } else {
//                        logger.info("Agents collection already initialized with {} agents, skipping", count);
//                        return reactor.core.publisher.Mono.empty();
//                    }
//                })
//                .block(); // Block for initialization
//        };
//    }
}
