package pl.monify.triggers.dto;

import java.util.Map;

public class WebhookTriggerRequest {
    private Map<String, Object> payload;

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
