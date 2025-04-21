package pl.monify.agentgateway.communication.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BaseAgentMessage(String type) {
}
