package itb.sister.crdt.signal;

import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class SignalCommand implements Runnable {

    private static SignalServer signalServer;

    public SignalCommand() {
    }

    @Override
    public void run() {
        boolean stop = false;
        Scanner scan = new Scanner(System.in);

        while (!stop) {
            String command = scan.nextLine();

            if (command.equals("stop")) {
                try {
                    signalServer.stop();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
                stop = true;
            } else {
                System.out.println("Command not recognizable");
            }
        }
    }


    public static void main(String[] args) {
        BasicConfigurator.configure();

        String host = "localhost";
        int port = 8888;

        SignalCommand signalCommand = new SignalCommand();
        Thread thread = new Thread(signalCommand);
        thread.start();

        // Create signal server
        signalServer = new SignalServer(new InetSocketAddress(host, port));
        signalServer.start();
    }
}
