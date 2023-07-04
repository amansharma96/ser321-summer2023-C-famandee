package leader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Leader {
    private final int port;
    private final ConcurrentMap<String, Boolean> bookAvailability;

    public Leader(int port) {
        this.port = port;
        this.bookAvailability = new ConcurrentHashMap<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Leader node started. Listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String processRequest(String clientID, String request) {
        String[] requestParts = request.split(" ", 2);
        if (requestParts.length < 2) {
            return "Invalid command format.";
        }
        String command = requestParts[0];
        String book = requestParts[1];
        String response;

        switch (command) {
            case "BORROW":
                response = borrowBook(clientID, book);
                break;
            case "RETURN":
                response = returnBook(clientID, book);
                break;
            case "UPDATE":
                response = updateBookAvailability(book, Boolean.parseBoolean(requestParts[2]));
                break;
            default:
                response = "Invalid command";
                break;
        }

        return response;
    }

    private String borrowBook(String clientID, String book) {
        if (bookAvailability.containsKey(book) && bookAvailability.get(book)) {
            bookAvailability.put(book, false);
            return "Book " + book + " is borrowed successfully.";
        } else {
            return "Book " + book + " is not available.";
        }
    }

    private String returnBook(String clientID, String book) {
        if (bookAvailability.containsKey(book) && !bookAvailability.get(book)) {
            bookAvailability.put(book, true);
            return "Book " + book + " is returned successfully.";
        } else {
            return "Book " + book + " cannot be returned as it is not borrowed.";
        }
    }

    private String updateBookAvailability(String book, boolean isAvailable) {
        bookAvailability.put(book, isAvailable);
        return "Book availability updated.";
    }

    private class ClientHandler extends Thread {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }


        public void run() {
            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                String clientID = in.readLine();
                System.out.println("Connected to client: " + clientID);

                String input;
                while ((input = in.readLine()) != null) {
                    if (input.equalsIgnoreCase("EXIT")) {
                        // Exit command received, close the connection
                        break;
                    }

                    String response = processRequest(clientID, input);
                    out.println(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
