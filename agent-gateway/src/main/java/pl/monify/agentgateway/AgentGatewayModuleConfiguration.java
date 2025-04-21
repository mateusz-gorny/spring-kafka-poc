package pl.monify.agentgateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import pl.monify.agentgateway.agentdelivery.AgentDeliveryPortsConfiguration;
import pl.monify.agentgateway.config.JwtProperties;
import pl.monify.agentgateway.config.WebSocketProperties;
import pl.monify.agentgateway.token.TokenConfiguration;

@Configuration
@EnableConfigurationProperties({
        JwtProperties.class,
        WebSocketProperties.class
})
@Import({
        AgentDeliveryPortsConfiguration.class,
        TokenConfiguration.class,
})
@EnableReactiveMongoRepositories
public class AgentGatewayModuleConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
