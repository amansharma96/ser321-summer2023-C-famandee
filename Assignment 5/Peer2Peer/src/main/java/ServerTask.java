import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.*;

public class ServerTask extends Thread {
	private BufferedReader bufferedReader;
	private Peer peer = null;
	private PrintWriter out = null;
	private Socket socket = null;

	public ServerTask(Socket socket, Peer peer) throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		this.peer = peer;
		this.socket = socket;
	}

	/**
	 * Reads incoming messages from the client and processes them based on their type.
	 * Closes the resources when the thread is interrupted or finished.
	 */
	public void run() {
		while (true) {
			try {
				// Read data from the client
				String receivedData = bufferedReader.readLine();
				if (receivedData == null) {
					break;
				}

				// Parse the received data as JSON
				JSONObject json = new JSONObject(receivedData);

				// Process the received message based on its type
				switch (json.getString("type")) {
					case "join":
						// Extract relevant information from the join request
						String username = json.getString("username");
						String ip = json.getString("ip");
						int port = json.getInt("port");
						boolean isLeader = json.getBoolean("leader");

						// Print a message indicating a user wants to join the network
						System.out.println(username + " wants to join the network");

						// Update the peer's list of peers to listen to
						peer.updateListenToPeers(ip + "-" + port + "-" + username + "-" + isLeader);

						// Create a response containing the updated peer list
						String peerListResponse = "{'type': 'join', 'list': '" + peer.getPeers() + "'}";

						// Send the response back to the client
						out.println(peerListResponse);

						// If the current peer is the leader, push the received message to all peers
						if (peer.isLeader()) {
							peer.pushMessage(json.toString());
							System.out.println("Leader received a message: " + json.toString());
						}

						break;
					default:
						// Extract the sender's username and message from the received JSON
						String senderUsername = json.getString("username");
						String message = json.getString("message");

						// Print the sender's username and message
						System.out.println("[" + senderUsername + "]: " + message);
						break;
				}
			} catch (Exception e) {
				// Interrupt the thread if an exception occurs
				interrupt();
				break;
			}
		}

		try {
			// Code to close resources
			bufferedReader.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
