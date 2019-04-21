package itb.sister.crdt.nodes;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class ServerNode extends WebSocketServer implements Runnable {

    private String webSocketAddress;
    private boolean startServer = false;

    public ServerNode(InetSocketAddress address) {
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

    @Override
    public void run() {
        if (!startServer) {
            System.out.println("asuu");
            startServer = true;
            this.start();
        }
    }

    public String getWebSocketAddress() {
        return webSocketAddress;
    }

    public void setWebSocketAddress(String webSocketAddress) {
        this.webSocketAddress = webSocketAddress;
    }
}
