package pl.monify.agent.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.monify.agent.auth.TokenClient;
import pl.monify.agent.gateway.GatewayClient;
import pl.monify.agent.task.TaskExecutor;

@Configuration
@EnableConfigurationProperties(AgentProperties.class)
public class AgentConfiguration {

    @Bean
    public OkHttpClient httpClient() {
        return new OkHttpClient();
    }

    @Bean
    public TokenClient tokenClient(OkHttpClient client, ObjectMapper mapper, AgentProperties props) {
        return new TokenClient(client, mapper, props);
    }

    @Bean(name = "agentTaskExecutor")
    public TaskExecutor taskExecutor() {
        return new TaskExecutor();
    }

    @Bean(initMethod = "connect")
    public GatewayClient gatewayClient(AgentProperties props,
                                       TokenClient tokenClient,
                                       OkHttpClient client,
                                       ObjectMapper mapper,
                                       TaskExecutor taskExecutor) {
        return new GatewayClient(props, tokenClient, client, mapper, taskExecutor);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
