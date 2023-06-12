import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 8080;
    private static JFrame frame; // Declare the JFrame as a class variable
    private static String playerName;

    private static void displayImage(String imagePath) {
        // Close the previous Swing window if it's already open
        if (frame != null) {
            frame.dispose();
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Image Guess");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            try {
                BufferedImage img = ImageIO.read(new File(imagePath));
                JLabel picLabel = new JLabel(new ImageIcon(img));
                frame.getContentPane().add(picLabel);
                frame.pack();
                frame.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void sendName(PrintWriter writer) {
        writer.println(playerName);
    }

    private static void handleLeaderboard(BufferedReader reader) throws IOException {
        String leaderboardJson = reader.readLine();
        JSONObject json = new JSONObject(leaderboardJson);
        System.out.println("----- Leaderboard -----");
        System.out.println(json.get("leaderboard"));
        System.out.println("-----------------------");
    }

    private static void playGame(BufferedReader reader, PrintWriter writer, Scanner scanner) throws IOException {
        int score = 0;
        boolean gameOver = false;
        while (!gameOver) {
            // Receive the JSON message from the server
            String jsonMessage = reader.readLine();
            JSONObject json = new JSONObject(jsonMessage);

            // Check if the game is over
            if (json.has("status")) {
                String status = json.getString("status");
                System.out.println("Game over. You " + status + "!");
                gameOver = true;
                return;
            }

            // Check if the response contains "imagePath" field
            if (json.has("imagePath")) {
                // Extract the image path and current score from the JSON message
                String imagePath = json.getString("imagePath");
                int currentPoints = json.getInt("currentPoints");

                // Display the image to the user
                displayImage(imagePath);

                // Prompt the user to enter their guess or request more/next
                System.out.println("Enter your guess or type 'more' for another image or 'next' to skip to the next country.");
                String userInput = scanner.nextLine();

                // Send the user's input to the server
                JSONObject userInputJson = new JSONObject();
                userInputJson.put("input", userInput);
                writer.println(userInputJson.toString());

                // Receive the server's response
                String responseJson = reader.readLine();
                JSONObject response = new JSONObject(responseJson);

                // Check if the response contains "response" field
                if (response.has("response")) {
                    String responseMessage = response.getString("response");
                    score = response.getInt("score");

                    // Print the server's response and the user's score
                    System.out.println(responseMessage);
                    System.out.println("Your score is: " + score);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to server: " + socket.getInetAddress().getHostAddress());

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);

            // Request and send the player's name
            System.out.println("Enter your name:");
            playerName = scanner.nextLine();
            sendName(writer);

            while (true) {
                // Prompt the user to choose between leaderboard or game
                System.out.println("\n----- Menu -----");
                System.out.println("1. Leaderboard");
                System.out.println("2. Play Game");
                System.out.println("3. Quit");
                System.out.println("Enter your choice:");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                // Send the user's choice to the server
                writer.println(choice);

                switch (choice) {
                    case 1:
                        // Handle leaderboard
                        handleLeaderboard(reader);
                        break;
                    case 2:
                        // Play the game
                        playGame(reader, writer, scanner);
                        break;
                    case 3:
                        // Quit the game
                        scanner.close();
                        reader.close();
                        writer.close();
                        socket.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
