package pl.monify.agentstatus.adapter.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.monify.agentstatus.domain.model.AgentPing;

import java.time.Instant;
import java.util.Map;

@Document("agent_pings")
public class AgentPingDocument {

    @Id
    private String id;
    private String agentId;
    private String sessionId;
    private String teamId;
    private Instant timestamp;
    private Map<String, Object> hostStats;

    public static AgentPingDocument from(AgentPing ping) {
        AgentPingDocument doc = new AgentPingDocument();
        doc.agentId = ping.agentId();
        doc.sessionId = ping.sessionId();
        doc.teamId = ping.teamId();
        doc.timestamp = ping.timestamp();
        doc.hostStats = ping.hostStats();
        return doc;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getHostStats() {
        return hostStats;
    }

    public void setHostStats(Map<String, Object> hostStats) {
        this.hostStats = hostStats;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
}
