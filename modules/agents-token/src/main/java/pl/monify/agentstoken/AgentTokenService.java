package pl.monify.agentstoken;

public interface AgentTokenService {
    boolean isValidSecret(String agentId, String secret);
}
