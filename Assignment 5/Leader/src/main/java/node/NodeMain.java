package node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NodeMain {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java node.NodeMain <leaderHost> <leaderPort> <bookFilePath>");
            System.exit(1);
        }

        String leaderHost = args[0];
        int leaderPort = Integer.parseInt(args[1]);
        String bookFilePath = args[2];

        try (BufferedReader reader = new BufferedReader(new FileReader(bookFilePath))) {
            Map<String, Boolean> bookAvailability = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    String bookName = parts[0].trim();
                    boolean isAvailable = Boolean.parseBoolean(parts[1].trim());
                    bookAvailability.put(bookName, isAvailable);
                }
            }

            Set<String> availableBooks = getAvailableBooks(bookAvailability);

            Node node = new Node(leaderHost, leaderPort, availableBooks);
            node.start();

            // Accept user input for tasks
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.print("Enter task (CHECK_AVAILABILITY/BORROW/RETURN/EXIT): ");
                String userInput = userInputReader.readLine();
                if (userInput.equalsIgnoreCase("exit")) {
                    break;
                }
                processTask(userInput, node);
            }

        } catch (IOException e) {
            System.out.println("Failed to read book file: " + bookFilePath);
            e.printStackTrace();
        }
    }

    private static Set<String> getAvailableBooks(Map<String, Boolean> bookAvailability) {
        Set<String> availableBooks = new HashSet<>();
        for (Map.Entry<String, Boolean> entry : bookAvailability.entrySet()) {
            if (entry.getValue()) {
                availableBooks.add(entry.getKey());
            }
        }
        return availableBooks;
    }

    private static void processTask(String userInput, Node node) {
        String[] taskParts = userInput.split(" ", 2);
        if (taskParts.length != 2) {
            System.out.println("Invalid task format. Please try again.");
            return;
        }

        String taskCommand = taskParts[0].trim().toUpperCase();
        String bookName = taskParts[1].trim();

        String response = sendTaskRequest(taskCommand, bookName, node);
        System.out.println("Response from leader node: " + response);
    }

    private static String sendTaskRequest(String command, String book, Node node) {
        try (Socket socket = new Socket(node.getLeaderHost(), node.getLeaderPort());
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send task request to the leader
            String request = command + " " + book;
            out.println(request);

            // Receive response from the leader
            return in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
            return "Error communicating with the leader node.";
        }
    }
}
