package pl.monify.agentgateway.token;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.monify.agentgateway.token.adapter.kafka.AgentCreatedKafkaListener;
import pl.monify.agentgateway.token.adapter.mongo.MongoAgentKeyRepository;
import pl.monify.agentgateway.token.adapter.mongo.MongoJwtKeyProvider;
import pl.monify.agentgateway.token.domain.port.out.JwtTokenKeyProviderPort;

@Configuration
public class TokenMongoConfig {

    @Bean
    public JwtTokenKeyProviderPort jwtTokenKeyProviderPort(MongoAgentKeyRepository repository) {
        return new MongoJwtKeyProvider(repository);
    }

    @Bean
    public AgentCreatedKafkaListener agentCreatedKafkaListener(MongoAgentKeyRepository repository) {
        return new AgentCreatedKafkaListener(repository);
    }
}
