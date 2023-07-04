package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        String serverHost = "localhost"; // Replace with actual server host
        int serverPort = 8888; // Replace with actual server port

        // Read client ID from user input
        String clientID = readClientID();


        Client client = new Client(serverHost, serverPort);
        client.start();
    }

    private static String readClientID() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter client ID: ");
        return scanner.nextLine();
    }
    private static String readUserInput() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
