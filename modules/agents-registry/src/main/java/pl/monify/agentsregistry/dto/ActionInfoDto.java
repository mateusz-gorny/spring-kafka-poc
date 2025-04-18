package pl.monify.agentsregistry.dto;

import java.util.Map;

public record ActionInfoDto(
        String name,
        String displayName,
        String teamId,
        Map<String, Object> inputSchema,
        Map<String, Object> outputSchema
) {
}
