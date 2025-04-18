package pl.monify.agentstoken;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "agent_credentials")
public class AgentCredentials {

    @Id
    private String agentId;

    private String secret;

    protected AgentCredentials() {
    }

    public AgentCredentials(String agentId, String secret) {
        this.agentId = agentId;
        this.secret = secret;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getSecret() {
        return secret;
    }
}
