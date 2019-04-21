package itb.sister.crdt.signal;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class SignalServer extends WebSocketServer {

    private Map<String, String> connectionList = new HashMap<>();

    public SignalServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection to " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);

        // Remove connection from list of host
        connectionList.remove(conn.getRemoteSocketAddress().toString());

        // Print debug
        System.out.println("Removing connection");
        System.out.println(connectionList.toString());

        // Send connection list
        String json = getConnectionList();
        broadcast(json);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received message from "	+ conn.getRemoteSocketAddress() + ": " + message);

        // Add connection to list of host
        connectionList.put(conn.getRemoteSocketAddress().toString(), message);

        // Print debug
        System.out.println("Adding connection");
        System.out.println(connectionList.toString());

        // Send connection list
        String json = getConnectionList();
        broadcast(json);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("An error occurred on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
    }

    @Override
    public void onStart() {
        System.out.println("Signal server started successfully");
    }

    private String getConnectionList() {
        Gson gson = new Gson();
        String[] nodesList = new String[connectionList.size()];

        int i = 0;
        for (Map.Entry<String, String> entry : connectionList.entrySet()) {
            nodesList[i] = entry.getValue();
            i++;
        }

        String json = gson.toJson(nodesList);
        return json;
    }


}