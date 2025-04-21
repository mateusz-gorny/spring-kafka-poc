package pl.monify.agent.ws;

import okhttp3.WebSocket;
import org.springframework.stereotype.Component;

@Component
public class SessionRegistry {

    private WebSocket socket;

    public void register(WebSocket socket) {
        this.socket = socket;
    }

    public WebSocket get() {
        if (socket == null) throw new IllegalStateException("No session");
        return socket;
    }

    public boolean connected() {
        return socket != null;
    }
}
