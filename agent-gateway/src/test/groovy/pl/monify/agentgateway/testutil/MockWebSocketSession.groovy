package pl.monify.agentgateway.testutil

import org.reactivestreams.Publisher
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.web.reactive.socket.CloseStatus
import org.springframework.web.reactive.socket.HandshakeInfo
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

import java.util.function.Function

class MockWebSocketSession implements WebSocketSession {

    private final HandshakeInfo handshakeInfo
    private final List<String> incomingJson
    final List<String> sentMessages = []
    final List<CloseStatus> closedWith = []

    private final DefaultDataBufferFactory bufferFactory = new DefaultDataBufferFactory()

    MockWebSocketSession(HandshakeInfo handshakeInfo, List<String> incomingMessages = []) {
        this.handshakeInfo = handshakeInfo
        this.incomingJson = new ArrayList<>(incomingMessages)
    }

    @Override
    WebSocketMessage textMessage(String payload) {
        DataBuffer buffer = bufferFactory.wrap(payload.getBytes())
        return new WebSocketMessage(WebSocketMessage.Type.TEXT, buffer)
    }

    @Override
    Mono<Void> send(Publisher<WebSocketMessage> messages) {
        return Flux.from(messages)
                .doOnNext { msg -> sentMessages << msg.getPayloadAsText() }
                .then()
    }

    @Override
    Flux<WebSocketMessage> receive() {
        return Flux.fromIterable(incomingJson).map { json -> textMessage(json) }
    }

    @Override
    Mono<Void> close(CloseStatus status) {
        closedWith << status
        return Mono.empty()
    }

    @Override
    HandshakeInfo getHandshakeInfo() {
        return handshakeInfo
    }

    // Stubbed/unused methods
    @Override WebSocketMessage binaryMessage(Function<DataBufferFactory, DataBuffer> factory) { return null }
    @Override WebSocketMessage pingMessage(Function<DataBufferFactory, DataBuffer> factory) { return null }
    @Override WebSocketMessage pongMessage(Function<DataBufferFactory, DataBuffer> factory) { return null }
    @Override boolean isOpen() { return true }
    @Override String getId() { return "mock-id" }
    @Override DataBufferFactory bufferFactory() { return bufferFactory }
    @Override Mono<Void> close() { return Mono.empty() }
    @Override Mono<CloseStatus> closeStatus() { return Mono.justOrEmpty(closedWith.last()) }
    @Override Map<String, Object> getAttributes() { return [:] }
}
