import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;
import java.io.PrintWriter;
import org.json.*;

public class Peer {
	private String username;
	private BufferedReader bufferedReader;
	private ServerThread serverThread;

	private Set<SocketInfo> peers = new HashSet<SocketInfo>();
	private boolean leader = false;
	private SocketInfo leaderSocket;

	public Peer(BufferedReader bufReader, String username, ServerThread serverThread) {
		this.username = username;
		this.bufferedReader = bufReader;
		this.serverThread = serverThread;
	}

	/**
	 * Sets the peer as a leader and assigns the leader socket information.
	 * @param leader Indicates whether the peer is a leader.
	 * @param leaderSocket The socket information of the leader.
	 */
	public void setLeader(boolean leader, SocketInfo leaderSocket) {
		this.leader = leader;
		this.leaderSocket = leaderSocket;
	}

	/**
	 * Checks if the peer is a leader.
	 * @return true if the peer is a leader, false otherwise.
	 */
	public boolean isLeader() {
		return leader;
	}

	/**
	 * Adds a new peer to the set of connected peers.
	 * @param si The socket information of the new peer.
	 */
	public void addPeer(SocketInfo si) {
		peers.add(si);
	}

	/**
	 * Returns a string representation of the known peers.
	 * @return A string containing the host, port, name, and leader status of each peer.
	 */
	public String getPeers() {
		String s = "";
		for (SocketInfo p : peers) {
			s = s + p.getHost() + "-" + p.getPort() + "-" + p.getName() + "-" + p.getLeader() + ";";
		}
		return s;
	}

	/**
	 * Returns the number of connected peers.
	 * @return The number of connected peers.
	 */
	public int getPeerCount() {
		return peers.size();
	}

	/**
	 * Updates the set of connected peers based on the received peer list.
	 * @param list The peer list received from the leader.
	 * @throws Exception if there is an error parsing the peer list.
	 */
	public void updateListenToPeers(String list) throws Exception {
		String[] peerList = list.split(";");
		for (String p : peerList) {
			String[] hostPort = p.split("-");

			// Skip adding the current peer to the list of peers
			if ((hostPort[0].equals("localhost") || hostPort[0].equals(serverThread.getHost()))
					&& Integer.parseInt(hostPort[1]) == serverThread.getPort()) {
				continue;
			}
			SocketInfo socketInfo = new SocketInfo(hostPort[0], Integer.parseInt(hostPort[1]), hostPort[2],
					Boolean.parseBoolean(hostPort[3]));
			peers.add(socketInfo);
		}
	}

	/**
	 * Sends a message to the leader and receives a response.
	 * @param message The message to send to the leader.
	 */
	public void commLeader(String message) {
		try {
			BufferedReader reader = null;
			Socket socket = null;

			try {
				// Connect to the leader using the leader's host and port
				socket = new Socket(leaderSocket.getHost(), leaderSocket.getPort());
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (Exception c) {
				if (socket != null) {
					socket.close();
				} else {
					System.out.println("UNABLE TO CONNECT TO " + leaderSocket.getHost() + ":" + leaderSocket.getPort());
				}
				return;
			}

			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			// Send the message to the leader
			out.println(message);
			System.out.println("[SENT] Message to leader: " + message);

			// Read the response from the leader
			String response = reader.readLine();
			System.out.println("[RECEIVED] Response from leader: " + response);

			// Parse the JSON response
			JSONObject json = new JSONObject(response);

			// Handle different message types from the leader
			switch (json.getString("type")) {
				case "join":
					// Update the list of peers based on the leader's response
					updateListenToPeers(json.getString("list"));
					System.out.println("[INFO] Updated list of peers: " + getPeers());
					break;
				default:
					System.out.println("[INFO] Unknown message type received from leader.");
					break;
			}
		} catch (Exception e) {
			System.out.println("Exception: Could not reach leader!");
			System.exit(2);
		}
	}

	/**
	 * Sends a message to all connected peers.
	 * @param message The message to send to the peers.
	 */
	public void pushMessage(String message) {
		try {
			System.out.println("Number of peers connected: " + peers.size());

			Set<SocketInfo> toRemove = new HashSet<SocketInfo>();
			BufferedReader reader = null;
			int counter = 0;

			// Send the message to all connected peers
			for (SocketInfo socketInfo : peers) {
				Socket socket = null;
				try {
					socket = new Socket(socketInfo.getHost(), socketInfo.getPort());
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				} catch (Exception c) {
					if (socket != null) {
						socket.close();
					} else {
						System.out.println("This peer is unresponsive: " + socketInfo.getHost() + ":" + socketInfo.getPort());

						if (socketInfo.getLeader()) {
							System.out.println("Removing leader peer!");
						} else {
							toRemove.add(socketInfo);
							System.out.println("Removing pawn peer!");
						}
						continue;
					}
					System.out.println("Issue: " + c);
				}

				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

				// Send the message to the peer
				System.out.println("Message sent to " + socketInfo.getName() + ": " + message);
				out.println(message);
				counter++;
				socket.close();
			}

			// Remove unresponsive peers from the list
			if (!toRemove.isEmpty()) {
				for (SocketInfo s : toRemove) {
					commLeader("{'type': 'remove','port':'" + s.getPort() + "'}");
				}
			}

			System.out.println("Message sent to " + counter + " peers");
		} catch (Exception e) {
			System.out.println("Could not send message!");
			System.exit(2);
		}
	}

	/**
	 * Prompts the user for input and handles different commands.
	 * @throws Exception if there is an error reading user input.
	 */
	public void askForInput() throws Exception {
		try {
			System.out.println("> You can now start chatting (type 'exit' to exit)");
			label: while (true) {
				String message = bufferedReader.readLine();
				switch (message) {
					case "exit":
						System.out.println("Goodbye!");
						break label;
					case "peers":
						// Display the list of known peers
						System.out.println("Known peers:");
						System.out.println(getPeers());
						break;
					default:
						// Send the user's message to other peers
						System.out.println("Message sent!");
						pushMessage(
								"{'type': 'message', 'username': '" + username + "','message':'" + message + "'}");
						break;
				}
			}
			System.exit(0);

		} catch (Exception e) {
			System.out.println("No input received!");
			System.exit(2);
		}
	}

	public static void main(String[] args) throws Exception {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

		if (args.length == 4) {
			System.out.println("Attempting to connect!");
		} else {
			System.out.println("Expected: <name(String)> <peer(String)> <leader(String)> <isLeader(bool-String)>");
			System.exit(0);
		}

		String username = args[0];
		String peerInfo = args[1];
		String leaderInfo = args[2];
		boolean isLeader = args[3].equalsIgnoreCase("true");

		String peerHost = null;
		int peerPort = 0;
		try {
			// Extract the host and port information from the peer argument
			peerHost = peerInfo.substring(0, peerInfo.indexOf(":"));
			peerPort = Integer.parseInt(peerInfo.substring(peerInfo.indexOf(":") + 1));
		} catch (Exception e) {
			System.out.println("Given peer information is invalid!");
			System.exit(2);
		}

		String leaderHost = null;
		int leaderPort = 0;
		try {
			// Extract the host and port information from the leader argument
			leaderHost = leaderInfo.substring(0, leaderInfo.indexOf(":"));
			leaderPort = Integer.parseInt(leaderInfo.substring(leaderInfo.indexOf(":") + 1));
		} catch (Exception e) {
			System.out.println("Given leader information is invalid!");
			System.exit(2);
		}

		ServerThread serverThread = new ServerThread(peerHost, peerPort, username, isLeader);
		Peer peer = new Peer(bufferedReader, username, serverThread);
		SocketInfo leaderSocket = new SocketInfo(leaderHost, leaderPort, "'Given Leader'", true);

		if (isLeader) {
			System.out.println("[SYSTEM]: " + username + " IS THE LEADER");
			peer.setLeader(true, leaderSocket);
		} else {
			System.out.println("[SYSTEM]: " + username + " IS A PAWN");
			peer.addPeer(leaderSocket);
			peer.setLeader(false, leaderSocket);
			peer.commLeader(
					"{'type': 'join', 'username': '" + username + "','ip':'" + serverThread.getHost() + "','port':'"
							+ serverThread.getPort() + "','leader':'" + serverThread.isLeader() + "'}");
		}

		System.out.println("Welcome " + username + "!");
		serverThread.setPeer(peer);
		serverThread.start();
		peer.askForInput();
	}
}
