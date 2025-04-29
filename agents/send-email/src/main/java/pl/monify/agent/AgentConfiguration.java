package pl.monify.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.monify.agent.auth.TokenClient;
import pl.monify.agent.config.AgentProperties;
import pl.monify.agent.gateway.GatewayClient;
import pl.monify.agent.infra.metrics.AgentHostStatsProvider;
import pl.monify.agent.registration.AgentRegistrationClient;
import pl.monify.agent.task.ActionTaskExecutor;
import pl.monify.agent.task.TestActionTaskExecutor;
import pl.monify.agent.ws.PingScheduler;
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
    public TokenClient tokenClient(OkHttpClient client, ObjectMapper mapper, AgentProperties props, ActionTaskExecutor[] actionTaskExecutors) {
        return new TokenClient(client, mapper, props, actionTaskExecutors);
    }

    @Bean
    public WebSocketListenerImpl wsListener(ObjectMapper mapper, SessionRegistry registry, ActionTaskExecutor[] actionTaskExecutors, AgentRegistrationClient registrationClient) {
        return new WebSocketListenerImpl(mapper, registry, actionTaskExecutors, registrationClient);
    }

    @Bean
    public GatewayClient gatewayClient(AgentProperties props,
                                       OkHttpClient client,
                                       TokenClient tokenClient,
                                       WebSocketListenerImpl listener) {
        return new GatewayClient(props, client, tokenClient, listener);
    }

    @Bean
    public TestActionTaskExecutor testActionTaskExecutor() {
        return new TestActionTaskExecutor();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public AgentRegistrationClient agentRegistrationClient(AgentProperties props, ObjectMapper mapper, SessionRegistry registry, ActionTaskExecutor[] actionTaskExecutors) {
        return new AgentRegistrationClient(props, mapper, registry, actionTaskExecutors);
    }

    @Bean
    public PingScheduler pingScheduler(@Value("${monify.agent.id}") String agentId, @Value("${monify.agent.team-id}") String teamId, SessionRegistry sessionRegistry, ObjectMapper mapper, AgentHostStatsProvider hostStatsProvider) {
        return new PingScheduler(agentId, teamId, sessionRegistry, mapper, hostStatsProvider);

    }
}
