package itb.sister.crdt.nodes;

import com.google.gson.Gson;
import itb.sister.crdt.models.Version;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

import itb.sister.crdt.models.CharInfo;
import itb.sister.crdt.models.Operation;

public class ServerPeerNode extends WebSocketServer {

    private String webSocketAddress;
    private Gson gson = new Gson();

    public ServerPeerNode(InetSocketAddress address) {
        super(address);
        this.webSocketAddress = "ws://" + address.getHostName() + ":" + address.getPort();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake clientHandshake) {
        System.out.println("New connection to " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received message from "	+ conn.getRemoteSocketAddress() + ": " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("An error occurred on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
    }

    @Override
    public void onStart() {
        System.out.println("Node server started successfully");
    }

    public String getWebSocketAddress() {
        return webSocketAddress;
    }

    public void broadcastInsertion(CharInfo data) {
        Operation operation = new Operation(data, true, ControllerNode.getVersionVector().getLocalVersion());

        String message = gson.toJson(operation);
        System.out.println(message);
        broadcast(message);
    }

    public void broadcastDeletion(CharInfo data) {
        Operation operation = new Operation(data, false, ControllerNode.getVersionVector().getLocalVersion());

        String message = gson.toJson(operation);
        System.out.println(message);
        broadcast(message);
    }

}
