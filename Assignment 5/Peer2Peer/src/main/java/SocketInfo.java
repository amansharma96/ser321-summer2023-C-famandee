public class SocketInfo {
	private String host;
	private int port;
	private String name;
	private boolean leader;

	/**
	 * Constructs a SocketInfo object with the provided connection details.
	 *
	 * @param host   The host address of the connection.
	 * @param port   The port number of the connection.
	 * @param name   The name associated with the connection.
	 * @param leader Flag indicating if the connection is the leader.
	 */
	public SocketInfo(String host, int port, String name, boolean leader) {
		this.host = host;
		this.port = port;
		this.name = name;
		this.leader = leader;
	}

	/**
	 * Gets the port number of the connection.
	 *
	 * @return The port number.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Gets the host address of the connection.
	 *
	 * @return The host address.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Gets the name associated with the connection.
	 *
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Checks if the connection is the leader.
	 *
	 * @return true if the connection is the leader, false otherwise.
	 */
	public boolean getLeader() {
		return leader;
	}
}
