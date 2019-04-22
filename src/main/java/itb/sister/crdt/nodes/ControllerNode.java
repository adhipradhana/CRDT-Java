package itb.sister.crdt.nodes;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import com.google.gson.Gson;
import org.apache.log4j.BasicConfigurator;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class ControllerNode extends WebSocketClient  {

    private  Set<String> serverList = new HashSet<>();
    private  Set<String> previousServerList = new HashSet<>();

    private static Map<String, ClientPeerNode> clientPeerNodes = new HashMap<>();
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
        for (String serverAddress : serverList) {
            if (!previousServerList.contains(serverAddress)) {
                try {
                    ClientPeerNode peerNode = new ClientPeerNode(new URI(serverAddress), serverAddress);
                    peerNode.connect();
                    previousServerList.add(serverAddress);
                    clientPeerNodes.put(serverAddress, peerNode);
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void parseConnectionList(String message) {
        Gson gson = new Gson();
        String[] allConnections = gson.fromJson(message, String[].class);

        for (int i = 0; i < allConnections.length; i++) {
            if (!allConnections[i].equals(nodeServerAddress)) {
                serverList.add(allConnections[i]);
            }
        }

        System.out.println("Server list");
        for (String serverAddress : serverList) {
            System.out.println(serverAddress);
        }
    }

    public static Map<String, ClientPeerNode> getClientPeerNodes() {
        return clientPeerNodes;
    }

    public static void main(String[] args) throws URISyntaxException {
        BasicConfigurator.configure();
        Random rand = new Random();
        String signalServerAddress = "ws://localhost:8888";

        String host = "localhost";
        int port = rand.nextInt(10000) + 40000;

        WebSocketClient client = new ControllerNode(new URI(signalServerAddress));
        client.connect();

        ServerPeerNode serverPeerNode = new ServerPeerNode(new InetSocketAddress(host, port));
        nodeServerAddress = serverPeerNode.getWebSocketAddress();
        serverPeerNode.start();

        interfaceNode = new InterfaceNode(nodeServerAddress);
        interfaceNode.setServerPeerNode(serverPeerNode);
        interfaceNode.setClientSignal(client);
        InterfaceNode.getVersionVector().put(nodeServerAddress, 0);
        Thread interfaceThread = new Thread(interfaceNode);
        interfaceThread.start();
    }
}