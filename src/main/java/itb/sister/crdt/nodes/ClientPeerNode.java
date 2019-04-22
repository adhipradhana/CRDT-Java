package itb.sister.crdt.nodes;

import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import itb.sister.crdt.models.CRDT;

import java.net.URI;
import java.util.Map;

public class ClientPeerNode extends WebSocketClient {

    private Gson gson = new Gson();
    private String serverAddress;

    public ClientPeerNode(URI serverURI, String serverAddress) {
        super(serverURI);
        this.serverAddress = serverAddress;
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        System.out.println("New PEER connection opened");

        InterfaceNode.getVersionVector().put(serverAddress, 0);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);

        ControllerNode.getClientPeerNodes().remove(serverAddress);
        InterfaceNode.getVersionVector().remove(serverAddress);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received message: " + message);

        CRDT crdt = parseCRDT(message);
        updateVersionVector(crdt);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }

    public CRDT parseCRDT(String message) {
        CRDT crdt = new CRDT();
        crdt = gson.fromJson(message, crdt.getClass());

        return crdt;
    }

    public void updateVersionVector(CRDT crdt) {
        Map<String, Integer> oldVersionVector = InterfaceNode.getVersionVector();

        for (Map.Entry<String, Integer> entry : crdt.getVersionVector().entrySet()) {
            int operationCount = (entry.getValue() > oldVersionVector.get(entry.getKey())) ? entry.getValue() : oldVersionVector.get(entry.getKey());
            oldVersionVector.put(entry.getKey(), operationCount);
        }

        System.out.println("New Version Vector");
        System.out.println(oldVersionVector);
    }
}
