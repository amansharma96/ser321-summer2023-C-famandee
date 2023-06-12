import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private static final int PORT = 8080;
    private static final int WINNING_SCORE = 12;
    private static final int LOSING_SCORE = -3;
    private static Map<String, List<String>> countryImages = new HashMap<>();
    private static Map<String, Integer> playerScores = new HashMap<>();

    private static void updateLeaderboard(PrintWriter writer) {
        JSONObject leaderboardJson = new JSONObject();
        leaderboardJson.put("leaderboard", playerScores);
        writer.println(leaderboardJson.toString());
    }

    private static void handleClient(Socket clientSocket) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String playerName = reader.readLine();
            System.out.println("Player connected: " + playerName);
            System.out.println("Welcome, " + playerName + "!");

            while (true) {
                // Get the client's menu choice
                int choice = Integer.parseInt(reader.readLine());
                switch (choice) {
                    case 1:
                        // Send the leaderboard to the client
                        updateLeaderboard(writer);
                        break;
                    case 2:
                        // Play the game
                        playGame(playerName, writer, reader);
                        break;
                    default:
                        System.out.println("Invalid choice.");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void playGame(String playerName, PrintWriter writer, BufferedReader reader) throws IOException {
        int score = playerScores.getOrDefault(playerName, 0);
        boolean gameOver = false;
        Random random = new Random();
        List<String> countries = new ArrayList<>(countryImages.keySet());
        int imagesShown = 0;

        while (!gameOver) {
            int countryIndex = random.nextInt(countries.size());
            String country = countries.get(countryIndex);
            List<String> images = countryImages.get(country);
            int imageIndex = random.nextInt(images.size());
            String imageName = images.get(imageIndex);

            // Increment imagesShown each time an image is shown
            imagesShown++;

            String imagePath = "img/" + imageName;

            // Create a JSON object with the image data
            JSONObject json = new JSONObject();
            json.put("imagePath", imagePath);
            json.put("currentPoints", getPointsForImage(imageIndex, images.size()));
            json.put("score", score);

            // Send the JSON message to the client
            writer.println(json.toString());

            // Read the client's input
            String userInputJson = reader.readLine();
            JSONObject userInput = new JSONObject(userInputJson);
            String input = userInput.getString("input");

            // Process the user's input
            String response = "";
            if (input.equalsIgnoreCase("next")) {
                score -= 4;
                // Reset imagesShown
                imagesShown = 0;
                if (score < LOSING_SCORE) {
                    writer.println(getGameOverJson("lose"));
                    gameOver = true;
                } else {
                    response = "Moved to next country.";
                    json = new JSONObject();
                    json.put("response", response);
                    json.put("score", score);
                    writer.println(json.toString());
                }
            } else if (input.equalsIgnoreCase("more")) {
                if (imageIndex + 1 < images.size()) {

                    // Increment the image index and deduct points
                    imageIndex++;
                    score -= imagesShown;

                    response = "More images for this country.";
                    json = new JSONObject();
                    json.put("response", response);
                    json.put("score", score);
                    writer.println(json.toString());
                } else {
                    response = "No more images for this country.";
                    json = new JSONObject();
                    json.put("response", response);
                    json.put("score", score);
                    writer.println(json.toString());
                }
            }
            else {
                if (input.equalsIgnoreCase(country)) {
                    score += getPointsForImage(imageIndex, images.size());
                    if (score >= WINNING_SCORE) {
                        writer.println(getGameOverJson("win"));
                        gameOver = true;
                    } else {
                        response = "Correct guess.";
                        json = new JSONObject();
                        json.put("response", response);
                        json.put("score", score);
                        writer.println(json.toString());
                    }
                } else {
                    response = "Incorrect guess. Try again.";
                    json = new JSONObject();
                    json.put("response", response);
                    json.put("score", score);
                    writer.println(json.toString());
                }
            }
        }

        // Update the player's score in the leaderboard
        playerScores.put(playerName, score);
        updateLeaderboard(writer);
    }


    private static int getPointsForImage(int imageIndex, int totalImages) {
        if (imageIndex == 0) {
            return 5;
        } else if (imageIndex == totalImages - 1) {
            return 1;
        } else {
            return totalImages - imageIndex;
        }
    }

    private static String getResponse(int score, boolean gameOver) {
        if (gameOver) {
            return "Game over. You " + (score >= WINNING_SCORE ? "win!" : "lose!");
        } else {
            return (score > 0 ? "Correct guess!" : "Wrong guess!") + " Current score: " + score;
        }
    }

    private static String getGameOverJson(String status) {
        JSONObject gameOverJson = new JSONObject();
        gameOverJson.put("status", status);
        return gameOverJson.toString();
    }

    public static void main(String[] args) {
        File folder = new File("img");
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    String countryName = fileName.split("[0-9]")[0];
                    List<String> images = countryImages.getOrDefault(countryName, new ArrayList<>());
                    images.add(fileName);
                    countryImages.put(countryName, images);
                }
            }
        }

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started. Listening on port " + PORT + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Start a new thread to handle the client communication
                Thread thread = new Thread(() -> handleClient(clientSocket));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
