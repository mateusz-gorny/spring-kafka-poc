package pl.monify.agent.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.monify.agent.auth.TokenClient;
import pl.monify.agent.gateway.GatewayClient;
import pl.monify.agent.registration.AgentRegistrationClient;
import pl.monify.agent.task.AgentTaskExecutor;
import pl.monify.agent.ws.SessionRegistry;
import pl.monify.agent.ws.WebSocketListenerImpl;

@Configuration
@EnableConfigurationProperties(AgentProperties.class)
public class AgentConfiguration {

    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();
    }

    @Bean
    public TokenClient tokenClient(OkHttpClient client, ObjectMapper mapper, AgentProperties props) {
        return new TokenClient(client, mapper, props);
    }

    @Bean
    public WebSocketListenerImpl wsListener(ObjectMapper mapper, SessionRegistry registry, AgentTaskExecutor exec, AgentRegistrationClient registrationClient) {
        return new WebSocketListenerImpl(mapper, registry, exec, registrationClient);
    }

    @Bean
    public GatewayClient gatewayClient(AgentProperties props,
                                       OkHttpClient client,
                                       TokenClient tokenClient,
                                       WebSocketListenerImpl listener) {
        return new GatewayClient(props, client, tokenClient, listener);
    }

    @Bean
    public AgentTaskExecutor agentTaskExecutor() {
        return new AgentTaskExecutor();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public AgentRegistrationClient agentRegistrationClient(AgentProperties props, ObjectMapper mapper, SessionRegistry registry) {
        return new AgentRegistrationClient(props, mapper, registry);
    }
}
