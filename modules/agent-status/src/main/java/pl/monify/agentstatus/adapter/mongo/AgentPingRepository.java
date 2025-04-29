package pl.monify.agentstatus.adapter.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AgentPingRepository extends MongoRepository<AgentPingDocument, String> {
    Optional<AgentPingDocument> findTopByAgentIdOrderByTimestampDesc(String agentId);
    List<AgentPingDocument> findTop300ByAgentIdOrderByTimestampDesc(String agentId);
}
