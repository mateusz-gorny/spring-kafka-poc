package pl.monify.agentgateway.communication.web

import com.fasterxml.jackson.databind.ObjectMapper
import pl.monify.agentgateway.BaseIntegrationSpec
import pl.monify.agentgateway.TestMocksConfig
import pl.monify.agentgateway.communication.domain.port.out.ActionResultSenderPort

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class ExecutionResultWebSocketSpec extends BaseIntegrationSpec {

    def captured = []

    def setup() {
        TestMocksConfig.observerRegistry.reset()
        captured.clear()

        TestMocksConfig.observerRegistry.register(ActionResultSenderPort.class) {
            captured.add(it)
        }
    }

    def "should handle ActionExecutionResult message and forward it to result sender"() {
        given:
        def token = generateValidJwt(agentId: "agent-a", teamId: "team-a", action: "result")
        def latch = new CountDownLatch(1)
        def received = new AtomicReference<String>()
        def ws = connectWs(latch, received, token)

        def payload = [
                type: "ActionExecutionResult",
                correlationId: "abc-123",
                payload: [
                        status: "SUCCESS",
                        output: [key: "value"],
                        logs  : ["step1 ok", "step2 ok"]
                ]
        ]
        def message = new ObjectMapper().writeValueAsString(payload)

        when:
        ws.send(message)
        Thread.sleep(200)

        then:
        captured.size() == 1
        with(captured[0]) {
            assert it.actionInstanceId == "abc-123"
            assert it.status == "SUCCESS"
            assert it.output.key.asText() == "value"
            assert it.logs instanceof String[]
            assert it.logs == ["step1 ok", "step2 ok"]
        }

        cleanup:
        ws.cancel()
    }

    def "should return error when correlationId is missing"() {
        given:
        def token = generateValidJwt(agentId: "agent-b", teamId: "team-b", action: "result")
        def latch = new CountDownLatch(1)
        def received = new AtomicReference<String>()
        def ws = connectWs(latch, received, token)

        def message = '''
        {
          "type": "ActionExecutionResult",
          "payload": {
            "status": "SUCCESS",
            "output": {"x": 1},
            "logs": ["ok"]
          }
        }
        '''

        when:
        ws.send(message)
        latch.await(2, TimeUnit.SECONDS)

        then:
        received.get().contains('"type":"error"')
        received.get().contains('Invalid result payload')
        captured.isEmpty()

        cleanup:
        ws.cancel()
    }

    def "should return error when payload is missing"() {
        given:
        def token = generateValidJwt(agentId: "agent-c", teamId: "team-c", action: "result")
        def latch = new CountDownLatch(1)
        def received = new AtomicReference<String>()
        def ws = connectWs(latch, received, token)

        def message = '''
        {
          "type": "ActionExecutionResult",
          "correlationId": "abc-456"
        }
        '''

        when:
        ws.send(message)
        latch.await(2, TimeUnit.SECONDS)

        then:
        received.get().contains('"type":"error"')
        received.get().contains('Invalid result payload')
        captured.isEmpty()

        cleanup:
        ws.cancel()
    }

    def "should return error when JSON is invalid"() {
        given:
        def token = generateValidJwt(agentId: "agent-d", teamId: "team-d", action: "result")
        def latch = new CountDownLatch(1)
        def received = new AtomicReference<String>()
        def ws = connectWs(latch, received, token)

        def message = '{"type":"ActionExecutionResult", BAD_JSON}'

        when:
        ws.send(message)
        latch.await(2, TimeUnit.SECONDS)

        then:
        received.get().contains('"type":"error"')
        received.get().contains('Failed to parse incoming message, check JSON format')
        captured.isEmpty()

        cleanup:
        ws.cancel()
    }
}
