package pl.monify.agentgateway.communication.adapter.websocket;

import org.springframework.web.reactive.socket.WebSocketSession;

public record AgentSession(String agentId, String teamId, String correlationId, WebSocketSession session) {}
