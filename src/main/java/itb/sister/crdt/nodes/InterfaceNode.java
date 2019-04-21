package itb.sister.crdt.nodes;

import com.google.gson.Gson;
import itb.sister.crdt.models.CRDT;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.util.ArrayList;
import java.util.Scanner;

public class InterfaceNode implements Runnable {

    private ArrayList<ClientPeerNode> clientPeers = new ArrayList<>();
    private String siteId;

    private Gson gson = new Gson();
    volatile boolean shutdown = false;

    public void addClientPeer(ClientPeerNode clientPeerNode) {
        clientPeers.add(clientPeerNode);
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
                System.out.println("Shutting down thread");
                shutdown = true;
            } else if (command.equals("")) {
                System.out.println("NULL");
            } else {
                Character value = command.charAt(0);
                CRDT crdt = new CRDT(siteId, value, true, new int[]{1});

                String json = gson.toJson(crdt);
                for (int i = 0; i < clientPeers.size(); i++) {
                    try {
                        clientPeers.get(i).send(json);
                    } catch (WebsocketNotConnectedException e) {
                        clientPeers.remove(i);
                    }
                }
            }
        }
    }
}
