package node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Node {
    private final String leaderHost;
    private final int leaderPort;
    private final Set<String> availableBooks;

    public Node(String leaderHost, int leaderPort, Set<String> availableBooks) {
        this.leaderHost = leaderHost;
        this.leaderPort = leaderPort;
        this.availableBooks = availableBooks;
    }

    public String getLeaderHost() {
        return leaderHost;
    }

    public int getLeaderPort() {
        return leaderPort;
    }

    public void start() {
        try (Socket socket = new Socket(leaderHost, leaderPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Connected to leader node.");

            // Send initial book list to the leader
            out.println(String.join(",", availableBooks));

            // Start listening for requests from the leader
            String input;
            while ((input = in.readLine()) != null) {
                String response = processRequest(input);
                out.println(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String processRequest(String request) {
        String[] requestParts = request.split(" ", 2);
        String command = requestParts[0];
        String book = requestParts[1];
        String response;

        if (command.equals("CHECK_AVAILABILITY")) {
            response = checkBookAvailability(book);
        } else if (command.equals("BORROW")) {
            response = borrowBook(book);
        } else if (command.equals("RETURN")) {
            response = returnBook(book);
        } else {
            response = "Invalid command";
        }

        return response;
    }

    private String checkBookAvailability(String book) {
        if (availableBooks.contains(book)) {
            return "AVAILABLE";
        } else {

            return "NOT_AVAILABLE";
        }
    }

    private String borrowBook(String book) {
        if (availableBooks.contains(book)) {
            availableBooks.remove(book);
            return "SUCCESS";
        } else {
            return "FAILURE";
        }
    }

    private String returnBook(String book) {
        if (!availableBooks.contains(book)) {
            availableBooks.add(book);
            return "SUCCESS";
        } else {
            return "FAILURE";
        }
    }
}
