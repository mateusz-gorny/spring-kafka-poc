package pl.monify.agentstoken;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgentCredentialsRepository extends JpaRepository<AgentCredentials, String> {
    Optional<AgentCredentials> findByAgentId(String agentId);
}
