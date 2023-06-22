package taskone;

import java.net.ServerSocket;
import java.net.Socket;

import taskone.Performer;
import taskone.StringList;

public class ThreadedServer {

    public static void main(String args[]) throws Exception {
        ServerSocket serverSocket;
        StringList strings = new StringList();
        int port = 8000;

        if (args.length != 1) {
            System.out.println("Usage: gradle runServer -Pport=9099 -q --console=plain");
            System.exit(0);
        }
        
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be an integer");
            System.exit(1);
        }

        serverSocket = new ServerSocket(port);
        System.out.println("Server started at port: " + port);

        try {
            while (true) {
                System.out.println("Waiting for client connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected! Starting new thread...");
                ServerRunnable newClient = new ServerRunnable(clientSocket, strings);
                new Thread(newClient).start();
            }
        } catch (Exception e) {
            System.out.println("Server closing due to an error.");
            serverSocket.close();
            System.exit(0);
        }

    }

    private static class ServerRunnable implements Runnable {

        private Socket clientSocket;
        private StringList strings;

        ServerRunnable(Socket clientSocket, StringList strings) {
            this.clientSocket = clientSocket;
            this.strings = strings;
        }

        public void run() {
            try {
                Performer performer = new Performer(clientSocket, strings);
                performer.doPerform();
            } catch (Exception e) {
                System.out.println("Error occurred while handling client request: " + e.getMessage());
            } finally {
                System.out.println("Client disconnected.");
                try {
                    clientSocket.close();
                } catch (Exception e) {
                    System.out.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }
}

