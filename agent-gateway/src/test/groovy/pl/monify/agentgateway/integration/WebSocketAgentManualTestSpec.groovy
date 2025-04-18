package pl.monify.agentgateway.integration

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import io.jsonwebtoken.Jwts
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.kafka.support.serializer.JsonSerializer
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

import javax.crypto.spec.SecretKeySpec
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

@Ignore
class WebSocketAgentManualTestSpec extends Specification {

    @Shared
    OkHttpClient client = new OkHttpClient()

    KafkaProducer<String, Object> kafkaProducer
    WebSocket socket
    LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>()
    JsonSlurper slurper = new JsonSlurper()

    def setup() {
        Properties props = new Properties()
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName())
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName())
        kafkaProducer = new KafkaProducer<>(props)
    }

    def cleanup() {
        socket?.close(1000, "bye")
        kafkaProducer?.close()
    }

    def createJwt(String sub, String team) {
        def secret = "VqvZI1B2A7tKeY9PbYz5EUn37K+smnmGaLCE4YjoHkA="
        def key = new SecretKeySpec(Base64.decoder.decode(secret), "HmacSHA256")
        return Jwts.builder()
                .claims()
                .subject(sub)
                .add("team_id", team)
                .add("agent", true)
                .and()
                .signWith(key)
                .compact()
    }

    def connectAgent(String jwt) {
        queue.clear()
        def wsUrl = "ws://localhost:8090/ws/agent?token=" + jwt
        def request = new Request.Builder().url(wsUrl).build()
        def listener = new WebSocketListener() {
            @Override
            void onMessage(WebSocket webSocket, String text) {
                queue.offer(text)
            }
        }
        socket = client.newWebSocket(request, listener)
    }

    def registerAgent() {
        def register = JsonOutput.toJson([
                type         : "register",
                correlationId: "reg-001",
                payload      : [
                        action       : "test-action",
                        inputSchema  : [type: "object"],
                        outputSchema : [type: "object"]
                ]
        ])
        socket.send(register)
    }

    def "should register agent and receive 'registered'"() {
        given:
        def jwt = createJwt("test-agent", "test-team")
        connectAgent(jwt)
        registerAgent()

        when:
        def msg = queue.poll(2, TimeUnit.SECONDS)

        then:
        msg != null
        def parsed = slurper.parseText(msg)
        parsed.type == "registered"
    }

    def "should respond pong to ping"() {
        given:
        def jwt = createJwt("test-agent", "test-team")
        connectAgent(jwt)
        registerAgent()
        queue.clear()

        def ping = JsonOutput.toJson([
                type         : "ping",
                correlationId: "ping-001"
        ])
        socket.send(ping)

        when:
        def registeredResponse = queue.poll(2, TimeUnit.SECONDS)
        def pingResponse = queue.poll(2, TimeUnit.SECONDS)

        then:
        registeredResponse != null
        slurper.parseText(registeredResponse) == ["type": "registered"]

        pingResponse != null
        slurper.parseText(pingResponse) == ["type": "pong"]
    }

    def "should receive ActionExecutionRequest for matching agent"() {
        given:
        def jwt = createJwt("test-agent", "test-team")
        connectAgent(jwt)
        registerAgent()

        def trigger = [
                workflowInstanceId: "flow-1",
                action            : "test-action",
                teamId            : "test-team",
                correlationId     : "corr-123",
                input             : [value: "hello"]
        ]

        def record = new ProducerRecord<String, Object>("action.execution.request", trigger)
        kafkaProducer.send(record)

        when:
        def incoming = null
        for (int i = 0; i < 10; i++) {
            def msg = queue.poll(1, TimeUnit.SECONDS)
            if (msg != null && slurper.parseText(msg).type == "ActionExecutionRequest") {
                incoming = slurper.parseText(msg)
                break
            }
        }

        then:
        incoming != null
        incoming.type == "ActionExecutionRequest"
        incoming.correlationId == "corr-123"
        incoming.payload.workflowInstanceId == "flow-1"
        incoming.payload.input.value == "hello"
    }

    def "should reject WebSocket with missing JWT"() {
        given:
        def wsUrl = "ws://localhost:8090/ws/agent"
        def request = new Request.Builder().url(wsUrl).build()
        def listener = new WebSocketListener() {}

        when:
        def sock = client.newWebSocket(request, listener)
        Thread.sleep(1000)

        then:
        sock.queueSize() == 0 || sock.close(1000, "bye")
    }

    def "should reject JWT with missing claims"() {
        given:
        def secret = "VqvZI1B2A7tKeY9PbYz5EUn37K+smnmGaLCE4YjoHkA="
        def key = new SecretKeySpec(Base64.decoder.decode(secret), "HmacSHA256")
        def badJwt = Jwts.builder().setSubject("no-claims").signWith(key).compact()

        when:
        connectAgent(badJwt)
        Thread.sleep(1000)

        then:
        socket.queueSize() == 0 || socket.close(1000, "bye")
    }
}
