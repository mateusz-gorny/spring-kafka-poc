package pl.monify.agentgateway.communication.application;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.agentgateway.communication.domain.port.in.HandleActionExecutionResultUseCase;
import pl.monify.agentgateway.communication.domain.port.out.ActionResultSenderPort;

import java.util.HashMap;
import java.util.Map;

public class HandleActionExecutionResultService implements HandleActionExecutionResultUseCase {

    private static final Logger log = LoggerFactory.getLogger(HandleActionExecutionResultService.class);

    private final ActionResultSenderPort resultSender;

    public HandleActionExecutionResultService(ActionResultSenderPort resultSender) {
        this.resultSender = resultSender;
    }

    @Override
    public void handle(String correlationId, String status, JsonNode output, String[] logs) {
        log.info("[WS] Received result for correlationId={}, status={}", correlationId, status);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("correlationId", correlationId);
        resultMap.put("status", status);
        resultMap.put("output", output);
        resultMap.put("logs", logs);

        resultSender.send(resultMap);
    }
}
