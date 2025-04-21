package pl.monify.agentgateway.communication.web

import pl.monify.agentgateway.BaseIntegrationSpec
import pl.monify.agentgateway.TestMocksConfig
import pl.monify.agentgateway.communication.domain.model.AgentRegisteredMessage
import pl.monify.agentgateway.communication.domain.port.out.AgentRegisteredEventSenderPort

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class RegisterWebSocketSpec extends BaseIntegrationSpec {

    def captured = []

    def setup() {
        TestMocksConfig.observerRegistry.reset()
        captured.clear()
        TestMocksConfig.observerRegistry.register(AgentRegisteredEventSenderPort.class) {
            captured.add(it)
        }
    }

    def "should register agent and emit AgentRegisteredMessage"() {
        given:
        def agentId = "agent-123"
        def teamId = "team-abc"
        def action = "test-action"
        def token = generateValidJwt(agentId: agentId, teamId: teamId, action: action)

        def latch = new CountDownLatch(1)
        def received = new AtomicReference<String>()
        def ws = connectWs(latch, received, token)

        when:
        ws.send('''
        {
          "type": "register",
          "payload": {
            "action": "test-action",
            "inputSchema": {},
            "outputSchema": {}
          }
        }
        ''')
        latch.await(2, TimeUnit.SECONDS)

        then:
        received.get() == '{"type":"registered"}'
        captured.size() == 1
        with((AgentRegisteredMessage) captured[0]) {
            assert sessionId
            assert action == "test-action"
            assert teamId == "team-abc"
            assert inputSchema.isObject()
            assert inputSchema.size() == 0
            assert outputSchema.isObject()
            assert outputSchema.size() == 0
        }

        cleanup:
        ws.cancel()
    }

    def "should reject register with missing payload"() {
        given:
        def token = generateValidJwt(agentId: "agent-x1", teamId: "team-x", action: "act-x")
        def latch = new CountDownLatch(1)
        def received = new AtomicReference<String>()
        def ws = connectWs(latch, received, token)

        when:
        ws.send('{"type":"register"}')
        latch.await(2, TimeUnit.SECONDS)

        then:
        received.get().contains('"type":"error"')
        received.get().contains('invalid payload')
        captured.isEmpty()

        cleanup:
        ws.cancel()
    }

    def "should reject register with missing action"() {
        given:
        def token = generateValidJwt(agentId: "agent-x2", teamId: "team-x", action: "act-x")
        def latch = new CountDownLatch(1)
        def received = new AtomicReference<String>()
        def ws = connectWs(latch, received, token)

        when:
        ws.send('''
        {
          "type": "register",
          "payload": {
            "inputSchema": {},
            "outputSchema": {}
          }
        }
        ''')
        latch.await(2, TimeUnit.SECONDS)

        then:
        received.get().contains('"type":"error"')
        received.get().contains('invalid payload')
        captured.isEmpty()

        cleanup:
        ws.cancel()
    }

    def "should reject register with empty action"() {
        given:
        def token = generateValidJwt(agentId: "agent-x3", teamId: "team-x", action: "act-x")
        def latch = new CountDownLatch(1)
        def received = new AtomicReference<String>()
        def ws = connectWs(latch, received, token)

        when:
        ws.send('''
        {
          "type": "register",
          "payload": {
            "action": "",
            "inputSchema": {},
            "outputSchema": {}
          }
        }
        ''')
        latch.await(2, TimeUnit.SECONDS)

        then:
        received.get().contains('"type":"error"')
        received.get().contains('invalid payload')
        captured.isEmpty()

        cleanup:
        ws.cancel()
    }

    def "should return error for unknown message type"() {
        given:
        def token = generateValidJwt(agentId: "agent-x4", teamId: "team-x", action: "act-x")
        def latch = new CountDownLatch(1)
        def received = new AtomicReference<String>()
        def ws = connectWs(latch, received, token)

        when:
        ws.send('{"type":"unknownType"}')
        latch.await(2, TimeUnit.SECONDS)

        then:
        received.get().contains('"type":"error"')
        received.get().contains('unknown type')
        captured.isEmpty()

        cleanup:
        ws.cancel()
    }
}
