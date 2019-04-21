package itb.sister.crdt.nodes;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.gson.Gson;
import org.apache.log4j.BasicConfigurator;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class ControllerNode extends WebSocketClient  {

    private Map<String, Boolean> previousServerList = new HashMap<>();
    private String[] serverList;

    private static InterfaceNode interfaceNode;
    private static String nodeServerAddress;

    public ControllerNode(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        System.out.println("New connection opened");
        send(nodeServerAddress);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received message: " + message);

        parseConnectionList(message);
        initializePeerToPeerConnection();
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }

    private void initializePeerToPeerConnection() {
        for (int i = 0; i < serverList.length; i++) {
            if (!previousServerList.containsKey(serverList[i]) || !previousServerList.get(serverList[i])) {
                try {
                    ClientPeerNode peerNode = new ClientPeerNode(new URI(serverList[i]));
                    peerNode.connect();
                    previousServerList.put(serverList[i], true);
                    interfaceNode.addClientPeer(peerNode);
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void parseConnectionList(String message) {
        Gson gson = new Gson();

        String[] allConnections = gson.fromJson(message, String[].class);
        serverList = new String[allConnections.length - 1];
        int count = 0;

        for (int i = 0; i < allConnections.length; i++) {
            if (!allConnections[i].equals(nodeServerAddress)) {
                serverList[count++] = allConnections[i];
            }
        }

        System.out.println("Server list");
        for (int i = 0; i < serverList.length; i++) {
            System.out.println(serverList[i]);
        }
    }

    public static void main(String[] args) throws URISyntaxException {
        BasicConfigurator.configure();
        Random rand = new Random();
        String signalServerAddress = "ws://localhost:8888";

        String host = "localhost";
        int port = rand.nextInt(10000) + 40000;

        WebSocketClient client = new ControllerNode(new URI(signalServerAddress));
        client.connect();

        ServerPeerNode serverNode = new ServerPeerNode(new InetSocketAddress(host, port));
        nodeServerAddress = serverNode.getWebSocketAddress();
        serverNode.start();

        interfaceNode = new InterfaceNode(nodeServerAddress);
        Thread interfaceThread = new Thread(interfaceNode);
        interfaceThread.start();
    }
}