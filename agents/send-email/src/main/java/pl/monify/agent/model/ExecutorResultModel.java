package pl.monify.agent.model;

import java.util.List;
import java.util.Map;

public record ExecutorResultModel(String status, Map<String, Object> output, List<String> logs) {
}
