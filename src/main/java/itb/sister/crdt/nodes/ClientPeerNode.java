package itb.sister.crdt.nodes;

import com.google.gson.Gson;
import itb.sister.crdt.models.CharInfo;
import itb.sister.crdt.models.Version;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import itb.sister.crdt.models.Operation;

import javax.naming.ldap.Control;

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
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);

        ControllerNode.getClientPeerNodes().remove(serverAddress);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received message: " + message);

        Operation operation = new Operation();
        operation = gson.fromJson(message, operation.getClass());

        handleRemoteOperation(operation);

        ControllerNode.InterfaceNode.setText(crdt.getText());
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }

    public void handleRemoteOperation(Operation operation) {
        if (ControllerNode.getVersionVector().hasBeenApplied(operation.getVersion())) return;

        if (operation.isOperationType()) {
            this.applyOperation(operation);
        } else {
            ControllerNode.getBuffer().add(operation);
        }

        this.processDeletionBuffer();
    }

    public void processDeletionBuffer() {
        int i = 0;
        Operation deleteOperation;

        for (int j = 0; j < ControllerNode.getBuffer().size(); j++) {
            System.out.println(ControllerNode.getBuffer().get(j).getData().getValue());
        }

        while (i < ControllerNode.getBuffer().size()) {
            deleteOperation = ControllerNode.getBuffer().get(i);

            if (this.hasInsertionBeenApplied(deleteOperation)) {
                System.out.println();
                this.applyOperation(deleteOperation);
                ControllerNode.getBuffer().remove(i);
            } else {
                i++;
            }
        }
    }

    public boolean hasInsertionBeenApplied(Operation operation) {
        Version charVersion = new Version(operation.getData().getSiteId(), operation.getData().getCounter());
        return ControllerNode.getVersionVector().hasBeenApplied(charVersion);
    }

    public void applyOperation(Operation operation) {
        CharInfo value = operation.getData();
        CharInfo newChar = new CharInfo(value.getValue(), value.getSiteId(), value.getPositions(), value.getCounter());

        if (operation.isOperationType()) {
            this.crdt.handleRemoteInsert(newChar);
        } else {
            this.crdt.handleRemoteDelete(newChar, operation.getVersion().getSiteId());
        }

        ControllerNode.getVersionVector().update(operation.getVersion());
    }

}
