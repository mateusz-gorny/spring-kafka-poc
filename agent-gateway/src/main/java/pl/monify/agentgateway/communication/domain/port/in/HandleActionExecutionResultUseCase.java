package pl.monify.agentgateway.communication.domain.port.in;

import com.fasterxml.jackson.databind.JsonNode;

public interface HandleActionExecutionResultUseCase {
    void handle(String correlationId, String status, JsonNode output, String[] logs);
}
