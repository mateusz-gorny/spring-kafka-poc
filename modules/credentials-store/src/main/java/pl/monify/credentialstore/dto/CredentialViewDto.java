package pl.monify.credentialstore.dto;

import pl.monify.credentialstore.model.CredentialType;

import java.util.Map;

public class CredentialViewDto {
    private String id;
    private String name;
    private String domain;
    private String username;
    private String password;
    private CredentialType type;
    private Map<String, Object> extra;

    public CredentialViewDto() {}

    public CredentialViewDto(String id, String name, String domain, String username, String password, CredentialType type, Map<String, Object> extra) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.username = username;
        this.password = password;
        this.type = type;
        this.extra = extra;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CredentialType getType() {
        return type;
    }

    public void setType(CredentialType type) {
        this.type = type;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}
