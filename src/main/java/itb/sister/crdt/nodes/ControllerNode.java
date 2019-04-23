package itb.sister.crdt.nodes;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class ControllerNode extends WebSocketClient  {

    private  Set<String> serverList = new HashSet<>();
    private  Set<String> previousServerList = new HashSet<>();

    private static Map<String, ClientPeerNode> clientPeerNodes = new HashMap<>();
    private static VersionVector versionVector;
    private static CRDT crdt;

    private static String nodeServerAddress;
    private static ServerPeerNode serverPeerNode;
    private static WebSocketClient client;

    public ControllerNode(URI serverURI) {
        super(serverURI);
    }

    public static VersionVector getVersionVector() {
        return versionVector;
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

    public static class InterfaceNode extends Application {

        private WebSocketClient clientSignal;
        private ServerPeerNode innerServerPeerNode;
        private String siteId;
        private TextArea textArea;

        private Gson gson = new Gson();
        volatile boolean shutdown = false;

        public void setServerPeerNode(ServerPeerNode serverPeerNode) {
            this.innerServerPeerNode = serverPeerNode;
        }

        public void setClientSignal(WebSocketClient clientSignal) {
            this.clientSignal = clientSignal;
        }

        public void setSiteId(String siteId) { this.siteId = siteId; }

        @Override
        public void start(Stage primaryStage) throws Exception {
            this.setServerPeerNode(serverPeerNode);
            this.setClientSignal(client);
            this.setSiteId(nodeServerAddress);

            textArea = new TextArea();

            HBox container  = new HBox(textArea);
            container.setAlignment(Pos.CENTER);
            container.setPadding(new Insets(10));

            HBox.setHgrow(textArea, Priority.ALWAYS);

            BorderPane pane = new BorderPane();
            pane.setCenter(container);

            textArea.textProperty().addListener((final ObservableValue<? extends String> observable,
                                                 final String oldValue, final String newValue) -> {

                // Initialize variables
                String flag;
                Character value;
                int pos = textArea.getCaretPosition();

                if (newValue.equals("stop") || oldValue.equals("stop")) {
                    try {
                        System.out.println("Shutting down thread");
                        innerServerPeerNode.stop();
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

                        if (newValue.length() > oldValue.length()) { // insertion
                            flag = "insert";
                            value = newValue.charAt(pos);
                        } else {
                            pos--;
                            flag = "delete";
                            value = oldValue.charAt(pos);
                        }

                        System.out.println("flag = " + flag + " - val = " + value + " - carpos = " + pos);
                        String message = "dojalque";
                        innerServerPeerNode.broadcast(message);

                    } catch (StringIndexOutOfBoundsException ex) {
                        System.out.println("Failed to send CRDT");
                    }
                }
            });

            primaryStage.setTitle("Tubes Sister");

            primaryStage.setScene(new Scene(pane, 300, 275));
            primaryStage.show();
        }

    }

    public static void main(String[] args) throws URISyntaxException {
        BasicConfigurator.configure();
        Random rand = new Random();
        String signalServerAddress = "ws://localhost:8888";

        String host = "localhost";
        int port = rand.nextInt(10000) + 40000;

        client = new ControllerNode(new URI(signalServerAddress));
        client.connect();

        serverPeerNode = new ServerPeerNode(new InetSocketAddress(host, port));
        nodeServerAddress = serverPeerNode.getWebSocketAddress();
        serverPeerNode.start();

        // create version vector and CRDT
        versionVector = new VersionVector();
        crdt = new CRDT(nodeServerAddress, versionVector);

        new Thread(() -> {
            Application.launch(InterfaceNode.class);
        }).start();
    }
}