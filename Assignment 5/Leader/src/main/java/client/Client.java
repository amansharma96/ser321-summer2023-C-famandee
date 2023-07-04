package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private final String serverHost;
    private final int serverPort;

    public Client(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void start() {
        try (Socket socket = new Socket(serverHost, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            boolean exitRequested = false;

            while (!exitRequested) {
                System.out.print("Enter command (BORROW/RETURN/EXIT): ");
                String command = userInput.readLine();

                if (command.equalsIgnoreCase("BORROW") || command.equalsIgnoreCase("RETURN")) {
                    System.out.print("Enter book name: ");
                    String bookName = userInput.readLine();
                    String request = command.toUpperCase() + " " + bookName;
                    out.println(request);
                    String response = in.readLine();
                    System.out.println("Response: " + response);
                } else if (command.equalsIgnoreCase("EXIT")) {
                    exitRequested = true;
                } else {
                    System.out.println("Invalid command");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
