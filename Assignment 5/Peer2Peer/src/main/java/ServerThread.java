import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
	private ServerSocket serverSocket;
	private SocketInfo socket;
	private Peer peer;

	/**
	 * Creates a new ServerThread to listen for incoming connections.
	 *
	 * @param host      The host address to bind the ServerSocket.
	 * @param port      The port number to bind the ServerSocket.
	 * @param name      The name associated with this ServerThread.
	 * @param isLeader  Flag indicating if this ServerThread is the leader.
	 * @throws IOException if an I/O error occurs when creating the ServerSocket.
	 */
	public ServerThread(String host, int port, String name, boolean isLeader) throws IOException {
		// Display the host and port connected
		System.out.println("Host connected: " + host);
		System.out.println("Port connected: " + port);

		// Create a SocketInfo object to store connection details
		socket = new SocketInfo(host, port, name, isLeader);

		// Create a ServerSocket to listen for incoming connections
		serverSocket = new ServerSocket(port);

		// Display the listening address and port
		System.out.println("Listening on: " + host + ":" + port);
	}

	public void setPeer(Peer peer) {
		this.peer = peer;
	}

	/**
	 * Get the host address associated with this ServerThread.
	 *
	 * @return the host address
	 */
	public String getHost() {
		return socket.getHost();
	}

	/**
	 * Get the port number associated with this ServerThread.
	 *
	 * @return the port number
	 */
	public int getPort() {
		return socket.getPort();
	}

	/**
	 * Get the name associated with this ServerThread.
	 *
	 * @return the name
	 */
	public String getPName() {
		return socket.getName();
	}

	/**
	 * Check if this ServerThread is the leader.
	 *
	 * @return true if the ServerThread is the leader, false otherwise
	 */
	public boolean isLeader() {
		return socket.getLeader();
	}

	public void run() {
		try {
			while (true) {
				// Accept incoming connections
				Socket sock = serverSocket.accept();
				System.out.println("Accepted incoming connection from: " + sock.getInetAddress());

				// Create and start a new ServerTask thread for the connection
				ServerTask serverTask = new ServerTask(sock, peer);
				serverTask.start();

				System.out.println("Started a new ServerTask thread for the connection.");
			}
		} catch (IOException e) {
			System.err.println("Error accepting incoming connection: " + e.getMessage());
		}
	}
}