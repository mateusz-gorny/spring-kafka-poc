package pl.monify.agentstatus.adapter.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("agents")
public class AgentSecretDocument {

    @Id
    private String id;
    private String name;
    private String secret;

    public AgentSecretDocument() {}

    public AgentSecretDocument(String id, String name, String secret) {
        this.id = id;
        this.name = name;
        this.secret = secret;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSecret() {
        return secret;
    }
}
