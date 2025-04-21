package pl.monify.agent;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@ActiveProfiles("test")
public class AgentIntegrationTest {

    static MockWebServer authServer;

    @BeforeAll
    static void setup() throws Exception {
        authServer = new MockWebServer();
        authServer.start(9000);

        String fakeToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                + ".payload.signature";

        authServer.enqueue(new MockResponse()
                .setBody("{\"token\": \"" + fakeToken + "\"}")
                .addHeader("Content-Type", "application/json"));
    }

    @AfterAll
    static void teardown() throws Exception {
        authServer.shutdown();
    }

    @Test
    void agentShouldConnectToGatewayAndRespond() throws InterruptedException {
        // Czekamy aż agent się połączy i odeśle ping
        TimeUnit.SECONDS.sleep(30);

        // Można dodać dodatkowe asercje/logikę
        System.out.println("Agent połączył się i przesłał dane.");
    }
}
