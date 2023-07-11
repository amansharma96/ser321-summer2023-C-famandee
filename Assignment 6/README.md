# GRPC Services and Registry

The following folder contains a Registry.jar which includes a Registering service where Nodes can register to allow clients to find them and use their implemented GRPC services. 

Some more detailed explanations will follow and please also check the build.gradle file

Before starting do a "gradle generateProto".

### Services

The Service Station provides the following services:

Parrot Service: Allows the user to send a message to the server, which echoes back the same message.

Joke Service: Retrieves a specified number of jokes from the server and displays them to the user.

Zoo: Interacts with the Zoo service, allowing the user to add animals to the zoo, make animals speak, and make all animals speak at once.

Calculator: Performs arithmetic calculations by sending expressions to the Calculator service and receiving the results.

Coffee: Controls the Coffee service, allowing the user to brew coffee, get a cup of coffee, and check the brewing status.

Restaurant: Manages the Restaurant service, enabling the user to generate bills based on the table number, number of plates, plate cost, and tip amount.

Taxi: Calculates the fare for a taxi ride based on the total distance and time traveled.

### Requirements
gradle runRegistryServer

gradle runNode 

gradle runClient

Menu will be open on client side once you run all these, and then you can select services according to it

### gradle runRegistryServer
Will run the Registry node on localhost (arguments are possible see gradle). This node will run and allows nodes to register themselves. 

The Server allows Protobuf, JSON and gRPC. We will only be using gRPC

### gradle runNode
Will run a node with an Echo and Joke service. The node registers itself on the Registry. You can change the host and port the node runs on and this will register accordingly with the Registry

### gradle runClient
Will run a client which will call the services from the node, it talks to the node directly not through the registry. At the end the client does some calls to the Registry to pull the services, this will be needed later.

### gradle runDiscovery
Will create a couple of threads with each running a node with services in JSON and Protobuf. This is just an example and not needed for assignment 6. 

### gradle testProtobufRegistration
Registers the protobuf nodes from runDiscovery and do some calls. 

### gradle testJSONRegistration
Registers the json nodes from runDiscovery and do some calls. 
