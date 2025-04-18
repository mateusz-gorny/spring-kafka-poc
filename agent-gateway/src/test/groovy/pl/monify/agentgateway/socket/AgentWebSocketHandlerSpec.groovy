package pl.monify.agentgateway.socket

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.security.SignatureException
import org.springframework.web.reactive.socket.HandshakeInfo
import pl.monify.agentgateway.config.WebSocketProperties
import pl.monify.agentgateway.handler.AgentMessageHandler
import pl.monify.agentgateway.testutil.MockWebSocketSession
import spock.lang.Specification

import java.util.stream.Stream

class AgentWebSocketHandlerSpec extends Specification {

    def objectMapper = new ObjectMapper()
    def dummyRegistry = new AgentRegistry()
    def dummyHandlers = Stub(List) {
        stream() >> Stream.empty()
    }
    def rateLimitService = new RateLimiterService(new WebSocketProperties("/ws/agent", new WebSocketProperties.RateLimiting(10, 50, 100)))

    def "should close with status 4000 if token is missing"() {
        given:
        def handshake = Mock(HandshakeInfo) {
            getUri() >> new URI("ws://localhost/ws/agent")
        }
        def session = new MockWebSocketSession(handshake)
        def handler = new AgentWebSocketHandler(
                Mock(JwtParser),
                objectMapper,
                dummyRegistry,
                dummyHandlers,
                rateLimitService
        )

        when:
        handler.handle(session).block()

        then:
        session.closedWith*.code.contains(4000)
    }

    def "should close with status 4001 if jwt is invalid"() {
        given:
        def handshake = Mock(HandshakeInfo) {
            getUri() >> new URI("ws://localhost/ws/agent?token=bad.jwt")
        }
        def session = new MockWebSocketSession(handshake)
        def jwtParser = Mock(JwtParser) {
            parseSignedClaims(_ as String) >> { throw new SignatureException("fail") }
        }
        def handler = new AgentWebSocketHandler(jwtParser, objectMapper, dummyRegistry, dummyHandlers, rateLimitService)

        when:
        handler.handle(session).block()

        then:
        session.closedWith*.code.contains(4001)
    }

    def "should close with status 4002 if claims are incomplete"() {
        given:
        def claims = Mock(Claims) {
            getSubject() >> null
        }
        def jws = Mock(Jws) { getPayload() >> claims }
        def jwtParser = Mock(JwtParser) {
            parseSignedClaims(_ as String) >> (Jws<Claims>) jws
        }

        def handshake = Mock(HandshakeInfo) {
            getUri() >> new URI("ws://localhost/ws/agent?token=xyz")
        }
        def session = new MockWebSocketSession(handshake)
        def handler = new AgentWebSocketHandler(jwtParser, objectMapper, dummyRegistry, dummyHandlers, rateLimitService)

        when:
        handler.handle(session).block()

        then:
        session.closedWith*.code.contains(4002)
    }

    def "should respond with pong on ping"() {
        given:
        def claims = minimalValidClaims()
        def jws = Mock(Jws) { getPayload() >> claims }
        def jwtParser = Mock(JwtParser) {
            parseSignedClaims(_ as String) >> (Jws<Claims>) jws
        }

        def pingHandler = Stub(AgentMessageHandler) {
            type() >> "ping"
            handle(_ as String, _ as AgentSession) >> { args -> args[1].sendText("{\"type\":\"pong\"}") }
        }

        def msg = '{"type":"ping","correlationId":"abc","payload":null}'
        def handshake = Mock(HandshakeInfo) {
            getUri() >> new URI("ws://localhost/ws/agent?token=ok")
        }
        def session = new MockWebSocketSession(handshake, [msg])
        def handler = new AgentWebSocketHandler(jwtParser, objectMapper, dummyRegistry, [pingHandler], rateLimitService)

        when:
        handler.handle(session).block()

        then:
        session.sentMessages.any { it.contains("pong") }
    }

    def "should respond with error on invalid json"() {
        given:
        def claims = minimalValidClaims()
        def jws = Mock(Jws) { getPayload() >> claims }
        def jwtParser = Mock(JwtParser) {
            parseSignedClaims(_ as String) >> (Jws<Claims>) jws
        }

        def handshake = Mock(HandshakeInfo) {
            getUri() >> new URI("ws://localhost/ws/agent?token=ok")
        }
        def session = new MockWebSocketSession(handshake, ["this is not json!"])
        def handler = new AgentWebSocketHandler(jwtParser, objectMapper, dummyRegistry, dummyHandlers, rateLimitService)

        when:
        handler.handle(session).block()

        then:
        session.sentMessages.any { it.contains("Invalid format") }
    }

    private Claims minimalValidClaims() {
        return Mock(Claims) {
            getSubject() >> "agent-123"
            get("team_id", String) >> "team-abc"
            get("action", String) >> "send-email"
        }
    }
}
