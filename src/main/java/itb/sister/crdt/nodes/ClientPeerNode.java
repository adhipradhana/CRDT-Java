package itb.sister.crdt.nodes;

import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import itb.sister.crdt.models.Operation;

public class ClientPeerNode extends WebSocketClient {

    private Gson gson = new Gson();
    private String serverAddress;
    private CRDT crdt;

    public ClientPeerNode(URI serverURI, String serverAddress, CRDT crdt) {
        super(serverURI);
        this.serverAddress = serverAddress;
        this.crdt = crdt;
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        System.out.println("New PEER connection opened");

        ControllerNode.getVersionVector().addSiteId(serverAddress, 0);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);

        ControllerNode.getClientPeerNodes().remove(serverAddress);
        ControllerNode.getVersionVector().removeSiteId(serverAddress);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received message: " + message);

        Operation operation = new Operation();
        operation = gson.fromJson(message, operation.getClass());

        if (operation.isOperationType()) {
            crdt.handleRemoteInsert(operation.getData(), operation.getSiteId());

            ControllerNode.InterfaceNode.setText(crdt.getText());
        } else {
            crdt.handleRemoteDelete(operation.getData(), operation.getSiteId());

            ControllerNode.InterfaceNode.setText(crdt.getText());
        }
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }


}
