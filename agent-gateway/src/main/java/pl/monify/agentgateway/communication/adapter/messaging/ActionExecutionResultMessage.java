package pl.monify.agentgateway.communication.adapter.messaging;

import com.fasterxml.jackson.databind.JsonNode;

public record ActionExecutionResultMessage(String type, String correlationId, Payload payload) {

    public record Payload(String status, JsonNode output, String[] logs) {
    }
}
