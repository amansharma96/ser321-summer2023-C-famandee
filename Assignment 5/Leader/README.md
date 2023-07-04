# Distributed Library System

The Distributed Library System project implements a distributed library system where multiple clients can borrow and return books from a central library. The system is designed to handle concurrent client requests and ensure data consistency across multiple nodes.

## Introduction
This project utilizes consensus algorithm architecture, where multiple nodes communicate with each other to coordinate book borrowing and returning operations. The system consists of three main components: Leader, Client, and Node.

- Leader: The Leader is responsible for coordinating the network and managing the availability of books. Clients communicate with the Leader to request book borrowing or returning.

- Client: Clients are the users who interact with the Leader. They can send requests to borrow or return books to the Leader.

- Node: Nodes represent the library nodes that store information about the availability of books. Each node maintains its own inventory of books and handles the actual borrowing and returning operations.

## Running the System
To run the Distributed Library System, follow the steps below:

1. Start the Leader: Run the following command to start the Leader on the default port:
   ```
   gradle runLeader
   ```
2. Start the Clients: Clients can be started to interact with the library system. Use the following command to start a Client:
   ```
   Running Client 1
   gradle runClient1
   
   
   Running Client 2
   gradle runClient2
   ```
3. Start the Nodes: Each library node needs to be started separately. Use the following command to start a Node, specifying the path to the node's book inventory file:
   ```
   Running Libaray Node conatinig books of Libray 1
   gradle runNode1
   
   Running Libaray Node conatinig books of Libray 1 
   gradle runNode2
   ```

## Features
The Distributed Library System includes the following features:

1. Borrowing Books: Clients can request to borrow a book from the library. The Leader will process the request and coordinate with the appropriate Node to check the availability of the book.

2. Returning Books: Clients can return a previously borrowed book to the library. The Leader will process the return request and coordinate with the appropriate Node to update the book's availability.

3. Multiple Nodes: The system supports multiple library nodes, each maintaining its own inventory of books. The Leader coordinates the communication between the Clients and the Nodes.


## Youtube Link - https://youtu.be/eGBEds2Erp0


