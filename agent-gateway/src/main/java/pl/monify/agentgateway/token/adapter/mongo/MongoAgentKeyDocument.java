package pl.monify.agentgateway.token.adapter.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("agent_keys")
public class MongoAgentKeyDocument {

    @Id
    private String agentId;
    private String secret;
    private String name;

    public MongoAgentKeyDocument() {}

    public MongoAgentKeyDocument(String agentId, String secret, String name) {
        this.agentId = agentId;
        this.secret = secret;
        this.name = name;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getSecret() {
        return secret;
    }

    public String getName() {
        return name;
    }
}
