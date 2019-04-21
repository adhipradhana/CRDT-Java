package itb.sister.crdt.nodes;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class ClientPeerNode extends WebSocketClient {

    public ClientPeerNode(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        System.out.println("New PEER connection opened");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received message: " + message);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }
}
