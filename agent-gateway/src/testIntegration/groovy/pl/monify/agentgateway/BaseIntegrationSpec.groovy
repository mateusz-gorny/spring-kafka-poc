package pl.monify.agentgateway

import io.jsonwebtoken.Jwts
import okhttp3.*
import spock.lang.Specification

import javax.crypto.spec.SecretKeySpec
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference

class BaseIntegrationSpec extends Specification {

    URI baseUri = TestServerHolder.baseUri
    int port = TestServerHolder.port

    HttpResponse<String> get(String path) {
        def client = HttpClient.newHttpClient()
        def request = HttpRequest.newBuilder()
                .uri(baseUri.resolve(path))
                .GET()
                .build()

        return client.send(request, HttpResponse.BodyHandlers.ofString())
    }

    HttpResponse<String> postJson(String path, String jsonBody) {
        def client = HttpClient.newHttpClient()
        def request = HttpRequest.newBuilder()
                .uri(baseUri.resolve(path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build()

        return client.send(request, HttpResponse.BodyHandlers.ofString())
    }

    String generateValidJwt(Map claims = [:]) {
        def keyId = claims.kid ?: "default"
        def secret = TestServerHolder.jwtSecrets.get(keyId)

        assert secret: "Missing JWT secret for keyId: $keyId"

        def key = new SecretKeySpec(Base64.decoder.decode(secret), "HmacSHA256")

        return Jwts.builder()
                .subject(claims.agentId ?: "agent-1")
                .claim("team_id", claims.teamId ?: "team-abc")
                .claim("action", claims.action ?: "ping")
                .claim("agent", true)
                .header().add("kid", keyId).and()
                .signWith(key)
                .compact()
    }

    WebSocket connectWs(
            CountDownLatch latch,
            AtomicReference<String> received,
            String token = null
    ) {
        def client = new OkHttpClient()
        def requestBuilder = new Request.Builder()
                .url("ws://localhost:$port/ws")

        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        def request = requestBuilder.build()

        return client.newWebSocket(request, new WebSocketListener() {
            @Override
            void onMessage(WebSocket webSocket, String text) {
                received.set(text)
                latch.countDown()
            }

            @Override
            void onFailure(WebSocket webSocket, Throwable t, Response response) {
                if (response != null) {
                    received.set("HTTP ${response.code()}")
                } else {
                    received.set("ERROR: ${t.message}")
                }
                latch.countDown()
            }

            @Override
            void onClosing(WebSocket webSocket, int code, String reason) {
                received.set("CLOSING: $code $reason")
                latch.countDown()
            }
        })
    }
}
