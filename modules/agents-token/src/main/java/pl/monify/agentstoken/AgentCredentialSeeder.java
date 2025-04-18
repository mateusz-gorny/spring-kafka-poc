package pl.monify.agentstoken;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AgentCredentialSeeder {

    private static final Logger log = LoggerFactory.getLogger(AgentCredentialSeeder.class);

    private final AgentCredentialsRepository repository;

    public AgentCredentialSeeder(AgentCredentialsRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        if (repository.findByAgentId("agent-7cec9fb6-0770-43b3-b4d4-1d970470b10d").isEmpty()) {
            repository.save(new AgentCredentials(
                    "agent-7cec9fb6-0770-43b3-b4d4-1d970470b10d",
                    "206bc6b9-72cd-421d-846b-31632fbc024c"
            ));
            log.info("Agent credentials saved");
        }
    }
}
