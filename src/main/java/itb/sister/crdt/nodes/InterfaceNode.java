package itb.sister.crdt.nodes;

import com.google.gson.Gson;
import itb.sister.crdt.models.CRDT;
import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class InterfaceNode implements Runnable {

    private static Map<String, Integer> versionVector = new HashMap<>();
    private WebSocketClient clientSignal;
    private ServerPeerNode serverPeerNode;
    private String siteId;
    private int operationCount = 0;

    private Gson gson = new Gson();
    volatile boolean shutdown = false;

    public static Map<String, Integer> getVersionVector() {
        return versionVector;
    }

    public void setServerPeerNode(ServerPeerNode serverPeerNode) {
        this.serverPeerNode = serverPeerNode;
    }

    public void setClientSignal(WebSocketClient clientSignal) {
        this.clientSignal = clientSignal;
    }

    public InterfaceNode(String siteId) {
        this.siteId = siteId;
    }

    @Override
    public void run() {
        Scanner scan = new Scanner(System.in);

        while (!shutdown) {
            String command = scan.nextLine();

            if (command.equals("stop")) {
                try {
                    System.out.println("Shutting down thread");
                    serverPeerNode.stop();
                    for (Map.Entry<String, ClientPeerNode> entry : ControllerNode.getClientPeerNodes().entrySet()) {
                        entry.getValue().close();
                    }
                    clientSignal.close();

                    shutdown = true;
                } catch (IOException | InterruptedException ex) {
                    System.out.println("Failed to stop");
                }
            } else {
                try {
                    Character value = command.charAt(0);
                    versionVector.put(siteId, ++operationCount);
                    CRDT crdt = new CRDT(siteId, value, true, new int[]{1}, versionVector);
                    String message = gson.toJson(crdt);
                    serverPeerNode.broadcast(message);
                } catch (StringIndexOutOfBoundsException ex) {
                    System.out.println("Failed to send CRDT");
                }
            }
        }
    }

}
