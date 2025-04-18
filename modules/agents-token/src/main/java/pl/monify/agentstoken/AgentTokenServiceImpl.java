package pl.monify.agentstoken;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AgentTokenServiceImpl implements AgentTokenService {

    private static final Logger log = LoggerFactory.getLogger(AgentTokenServiceImpl.class);

    private final AgentCredentialsRepository repository;

    public AgentTokenServiceImpl(AgentCredentialsRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isValidSecret(String agentId, String secret) {
        return repository.findByAgentId(agentId)
                .map(c -> c.getSecret().equals(secret))
                .orElse(false);
    }
}
