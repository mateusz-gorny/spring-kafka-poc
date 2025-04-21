package pl.monify.agent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "monify.agent")
public record AgentProperties(
        String name,
        String secret,
        String teamId,
        List<String> actions,
        String authUrl,
        String gatewayUrl
) {}
