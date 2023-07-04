## Distributed Algorithm: Peer to Peer

### Introduction
This project implements a basic peer-to-peer chat system, where all peers can communicate with each other. Each peer acts as both a client and a server, listening for connections from other peers.

To start the network, it is recommended to first start the leader, which is in charge of coordinating the network.

### Running the Leader
To start the leader on the default port using localhost, run the following command:
```
gradle runPeer -PisLeader=true -q --console=plain
```

If you want to customize the leader settings, you can provide additional parameters as follows:
```
gradle runPeer -PpeerName=Aman -Ppeer="localhost:8080" -PisLeader=true -q --console=plain
```
You can replace "localhost" with the appropriate IP address if running on a different machine.

### Running a Pawn
A pawn refers to a peer that is not the leader. To start a pawn, use the following command:
```
gradle runPeer -PpeerName=Aman1 -Ppeer="localhost:9000" -Pleader="localhost:8080" -q --console=plain
```
You can start as many pawns as you like, and they should all connect to the leader and the existing peers in the network.

If you want to customize the settings for a pawn, provide the following additional parameters:
```
gradle runPeer -PpeerName=Aman2 -Ppeer="localhost:9002" -Pleader="localhost:8080" -q --console=plain
```
The "isLeader" parameter is set to false by default, so you do not need to set it explicitly. The "leader" parameter needs to be the same for all peers, regardless of whether they are leaders or pawns.

### Requirements
1. **Adding a New Node**: To add a new node to the network at any time and have it automatically register with the other nodes, follow these steps:
	- Start a new node using the `gradle runPeer` command, providing the appropriate parameters (e.g., name, host, and port).
	- When starting the new node, specify the host and port of any existing node in the network.
	- The new node establishes a connection with the existing node and sends a join request, including its own host and port information.
	- The existing node receives the join request, adds the new node's details to its list of known nodes, and responds with a list of all known nodes in the network.
	- Upon receiving the response, the new node updates its own list of known nodes and establishes connections with them.
	- The new node is now integrated into the network and can communicate with other nodes.

2. **Registering an Offline Node**: If a node is not responding anymore or goes offline, another node can register its absence and inform the other nodes. The following steps can be taken:
	- Each node periodically sends  messages to all other nodes in the network.
	- If a node does not receive a  response from a peer within a specified timeout period, it considers the peer unresponsive or offline.
	- The node that identifies an unresponsive peer broadcasts a "peer removal" message to all other nodes in the network, indicating the details of the unresponsive peer.
	- Upon receiving the removal message, each node removes the unresponsive peer from its list of known nodes.
	- The unresponsive node can attempt to reconnect to the network by following the steps outlined in the "Adding a New Node" section.

3. **Contacting Existing Nodes**: When a new node is started, it should contact any already existing node in the network to establish

communication. The new node can be started using the `gradle runPeer` command, specifying the host and port of an existing node as parameters. The new node then sends a join request to the existing node, and upon successful registration, it becomes part of the network.

4. **Detecting Unresponsive Peers**: When a node sends a message to another peer, it should track the response. If a peer does not respond within a reasonable time, the sending node considers the peer unresponsive or offline. The sending node then follows the steps mentioned in the "Registering an Offline Node" section to inform the other nodes about the unresponsive peer and remove it from the network.

### Conclusion
The modifications described above allow for the dynamic addition of new nodes to a peer-to-peer network and the detection/removal of unresponsive nodes. The provided commands and instructions explain how to run the leader and pawns, customize the network settings, and fulfill the specified requirements.

Please note that the specific implementation details may vary depending on the programming language, libraries, and frameworks used. The provided approach serves as a general guideline to accomplish the required tasks in a peer-to-peer network.

For more details and a visual explanation, you can refer to the provided YouTube link.

https://youtu.be/ytwnqbCftBA
