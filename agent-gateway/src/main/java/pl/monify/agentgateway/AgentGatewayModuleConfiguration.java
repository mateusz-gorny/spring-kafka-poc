package pl.monify.agentgateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import pl.monify.agentgateway.config.JwtProperties;
import pl.monify.agentgateway.config.WebSocketProperties;

@Configuration
@EnableConfigurationProperties({
        JwtProperties.class,
        WebSocketProperties.class
})
@EnableMongoRepositories
public class AgentGatewayModuleConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
