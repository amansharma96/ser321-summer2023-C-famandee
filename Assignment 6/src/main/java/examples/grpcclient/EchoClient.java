package example.grpcclient;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;
import service.*;
import test.TestProtobuf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.google.protobuf.Empty; // needed to use Empty


/**
 * Client that requests `parrot` method from the `EchoServer`.
 */
public class EchoClient {
  private final EchoGrpc.EchoBlockingStub blockingStub;
  private final JokeGrpc.JokeBlockingStub blockingStub2;
  private final RegistryGrpc.RegistryBlockingStub blockingStub3;
  private final RegistryGrpc.RegistryBlockingStub blockingStub4;

  private final ZooGrpc.ZooBlockingStub zooStub;
  private final RecipeGrpc.RecipeBlockingStub calculatorStub;
  private final CoffeePotGrpc.CoffeePotBlockingStub coffeeStub;
  private final TaxiCompanyServiceGrpc.TaxiCompanyServiceBlockingStub taxiStub;
  private final RestaurantServiceGrpc.RestaurantServiceBlockingStub restaurantStub;





  /**
   * Construct client for accessing server using the existing channel.
   */
  public EchoClient(Channel channel, Channel regChannel) {
    blockingStub = EchoGrpc.newBlockingStub(channel);
    blockingStub2 = JokeGrpc.newBlockingStub(channel);
    blockingStub3 = RegistryGrpc.newBlockingStub(regChannel);
    blockingStub4 = RegistryGrpc.newBlockingStub(channel);
    zooStub = ZooGrpc.newBlockingStub(channel);
    calculatorStub = RecipeGrpc.newBlockingStub(channel);
    coffeeStub = CoffeePotGrpc.newBlockingStub(channel);
    restaurantStub = RestaurantServiceGrpc.newBlockingStub(channel);
    taxiStub = TaxiCompanyServiceGrpc.newBlockingStub(channel);

  }

  public void askServerToParrot(String message) {
    ClientRequest request = ClientRequest.newBuilder().setMessage(message).build();
    ServerResponse response;
    try {
      response = blockingStub.parrot(request);
      System.out.println("Server Parroted: " + response.getMessage());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e.getMessage());
    }
  }

  public void askForJokes(int num) {
    JokeReq request = JokeReq.newBuilder().setNumber(num).build();
    JokeRes response;

    // just to show how to use the empty in the protobuf protocol
    Empty empt = Empty.newBuilder().build();

    try {
      response = blockingStub2.getJoke(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
    System.out.println("Your jokes: ");
    for (String joke : response.getJokeList()) {
      System.out.println("--- " + joke);
    }
  }

  public void setJoke(String joke) {
    JokeSetReq request = JokeSetReq.newBuilder().setJoke(joke).build();
    JokeSetRes response;

    try {
      response = blockingStub2.setJoke(request);
      System.out.println(response.getOk());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void getNodeServices() {
    GetServicesReq request = GetServicesReq.newBuilder().build();
    ServicesListRes response;
    try {
      response = blockingStub4.getServices(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void getServices() {
    GetServicesReq request = GetServicesReq.newBuilder().build();
    ServicesListRes response;
    try {
      response = blockingStub3.getServices(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void findServer(String name) {
    FindServerReq request = FindServerReq.newBuilder().setServiceName(name).build();
    SingleServerRes response;
    try {
      response = blockingStub3.findServer(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void findServers(String name) {
    FindServersReq request = FindServersReq.newBuilder().setServiceName(name).build();
    ServerListRes response;
    try {
      response = blockingStub3.findServers(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void zooMenu(BufferedReader reader) throws IOException {
    boolean exit = false;

    while (!exit) {
      System.out.println("\nZoo Menu");
      System.out.println("1. Add an animal to the zoo");
      System.out.println("2. Make an animal speak");
      System.out.println("3. Make all animals speak");
      System.out.println("4. Exit Zoo Menu");
      System.out.println("Enter your choice:");

      int choice = Integer.parseInt(reader.readLine());

      switch (choice) {
        case 1:
          addAnimal(reader);
          break;
        case 2:
          makeAnimalSpeak(reader);
          break;
        case 3:
          makeAllAnimalsSpeak();
          break;
        case 4:
          exit = true;
          break;
        default:
          System.out.println("Invalid choice. Please enter a valid option.");
          break;
      }
    }
  }

  private void addAnimal(BufferedReader reader) throws IOException {
    System.out.println("Enter the animal's name:");
    String name = reader.readLine();

    System.out.println("Enter the animal's type:");
    String type = reader.readLine();

    System.out.println("Enter the sound the animal makes:");
    String sound = reader.readLine();

    Animal animal = Animal.newBuilder()
            .setName(name)
            .setType(type)
            .setAnimalSound(sound)
            .build();

    AddAnimalRequest request = AddAnimalRequest.newBuilder()
            .setAnimal(animal)
            .build();

    AddAnimalResponse response;

    try {
      response = zooStub.add(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }

    if (response.getIsSuccess()) {
      System.out.println(response.getMessage());
    } else {
      System.out.println("Error: " + response.getError());
    }
  }

  private void makeAnimalSpeak(BufferedReader reader) throws IOException {
    System.out.println("Enter the animal's name:");
    String name = reader.readLine();

    SpeakRequest request = SpeakRequest.newBuilder()
            .setName(name)
            .build();

    SpeakResponse response;

    try {
      response = zooStub.speak(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }

    if (response.getIsSuccess()) {
      System.out.println("Animal(s) speaking: " + response.getAnimalsList());
    } else {
      System.out.println("Error: " + response.getError());
    }
  }

  private void makeAllAnimalsSpeak() {
    Empty request = Empty.newBuilder().build();

    SpeakResponse response;

    try {
      response = zooStub.speakAll(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }

    if (response.getIsSuccess()) {
      System.out.println("Animal(s) speaking: " + response.getAnimalsList());
    } else {
      System.out.println("Error: " + response.getError());
    }
  }

  public void performCalculation(BufferedReader reader) throws IOException {
    System.out.println("Enter the first number:");
    int num1 = Integer.parseInt(reader.readLine());

    System.out.println("Enter the second number:");
    int num2 = Integer.parseInt(reader.readLine());

    System.out.println("Enter the operation (add, subtract, multiply, divide):");
    String operation = reader.readLine();

    Expression.Operation operationEnum;
    switch (operation.toLowerCase()) {
      case "add":
        operationEnum = Expression.Operation.PLUS;
        break;
      case "subtract":
        operationEnum = Expression.Operation.MINUS;
        break;
      case "multiply":
        operationEnum = Expression.Operation.MULTIPLY;
        break;
      case "divide":
        operationEnum = Expression.Operation.DIVIDE;
        break;
      default:
        System.out.println("Invalid operation. Please enter a valid operation (add, subtract, multiply, divide).");
        return;
    }

    Expression expression = Expression.newBuilder()
            .setNum1(num1)
            .setNum2(num2)
            .setOperation(operationEnum)
            .build();

    EvaluateRequest request = EvaluateRequest.newBuilder()
            .setExpression(expression)
            .build();

    EvaluateResponse response;

    try {
      response = calculatorStub.evaluate(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }

    if (response.getIsSuccess()) {
      System.out.println("Result: " + response.getResult());
    } else {
      System.out.println("Error: " + response.getError());
    }
  }

  public void coffeeMenu(BufferedReader reader) throws IOException {
    boolean exit = false;

    while (!exit) {
      System.out.println("\nCoffee Menu");
      System.out.println("1. Brew a cup of coffee");
      System.out.println("2. Get a cup of coffee");
      System.out.println("3. Check brew status");
      System.out.println("4. Exit Coffee Menu");
      System.out.println("Enter your choice:");

      int choice = Integer.parseInt(reader.readLine());

      switch (choice) {
        case 1:
          brewCoffee();
          break;
        case 2:
          getCupOfCoffee();
          break;
        case 3:
          checkBrewStatus();
          break;
        case 4:
          exit = true;
          break;
        default:
          System.out.println("Invalid choice. Please enter a valid option.");
          break;
      }
    }
  }

  private void brewCoffee() {
    Empty request = Empty.newBuilder().build();

    BrewResponse response;

    try {
      response = coffeeStub.brew(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }

    if (response.getIsSuccess()) {
      System.out.println("Coffee brewing has started");
    } else {
      System.out.println("Error: " + response.getError());
    }
  }

  private void getCupOfCoffee() {
    Empty request = Empty.newBuilder().build();

    GetCupResponse response;

    try {
      response = coffeeStub.getCup(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }

    if (response.getIsSuccess()) {
      System.out.println("Enjoy your cup of coffee");
    } else {
      System.out.println("Error: " + response.getError());
    }
  }

  private void checkBrewStatus() {
    Empty request = Empty.newBuilder().build();

    BrewStatusResponse response;

    try {
      response = coffeeStub.brewStatus(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }

    BrewStatus status = response.getStatus();

    System.out.println("Brew Status: " + status.getMessage());
    System.out.println("Time remaining: " + status.getMinutes() + " minutes, " + status.getSeconds() + " seconds");
  }

  private void makeBill(String tableNumber, int numPlates, double plateCost, double tipAmount) {
    GetBillRequest request = GetBillRequest.newBuilder()
            .setTableNumber(tableNumber)
            .setNumPlates(numPlates)
            .setPlateCost(plateCost)
            .setTipAmount(tipAmount)
            .build();

    GetBillResponse response;

    try {
      response = restaurantStub.getBill(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e.getMessage());
      return;
    }

    if (response.getIsSuccess()) {
      System.out.println("Bill details for Table " + tableNumber);
      System.out.println("Total Cost: $" + response.getTotalCost());
      System.out.println("Tip Amount: $" + response.getTipAmount());
      System.out.println("Total Amount: $" + response.getTotalAmount());
    } else {
      System.out.println("Error: " + response.getMessage());
    }
  }

  private void restaurantMenu(BufferedReader reader) throws IOException {
    boolean exit = false;

    while (!exit) {
      System.out.println("\nRestaurant Menu");
      System.out.println("1. Make a bill");
      System.out.println("2. Exit to Main Menu");
      System.out.println("Enter your choice:");

      int choice = Integer.parseInt(reader.readLine());

      switch (choice) {
        case 1:
          System.out.println("Enter the table number:");
          String tableNumber = reader.readLine();
          System.out.println("Enter the number of plates:");
          int numPlates = Integer.parseInt(reader.readLine());
          System.out.println("Enter the cost per plate:");
          double plateCost = Double.parseDouble(reader.readLine());
          System.out.println("Enter the tip amount:");
          double tipAmount = Double.parseDouble(reader.readLine());
          makeBill(tableNumber, numPlates, plateCost, tipAmount);
          break;
        case 2:
          exit = true;
          break;
        default:
          System.out.println("Invalid choice. Please enter a valid option.");
          break;
      }
    }
  }


  private void taxiMenu(BufferedReader reader) throws IOException {
    boolean exit = false;

    while (!exit) {
      System.out.println("\nTaxi Menu");
      System.out.println("1. Calculate Fare");
      System.out.println("2. Exit Taxi Menu");
      System.out.println("Enter your choice:");

      int choice = Integer.parseInt(reader.readLine());

      switch (choice) {
        case 1:
          calculateFare(reader);
          break;
        case 2:
          exit = true;
          break;
        default:
          System.out.println("Invalid choice. Please enter a valid option.");
          break;
      }
    }
  }

  private void calculateFare(BufferedReader reader) throws IOException {
    System.out.println("Enter the total distance in miles:");
    double totalDistance = Double.parseDouble(reader.readLine());

    System.out.println("Enter the total time in minutes:");
    int totalTime = Integer.parseInt(reader.readLine());

    FareRequest request = FareRequest.newBuilder()
            .setTotalDistance(totalDistance)
            .setTotalTime(totalTime)
            .build();

    FareResponse response;

    try {
      response = taxiStub.calculateFare(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }

    System.out.println("Total Fare: $" + response.getTotalFare());
  }


  public static void main(String[] args) throws Exception {
    if (args.length != 6) {
      System.out.println("Expected arguments: <host(String)> <port(int)> <regHost(string)> <regPort(int)> <message(String)> <regOn(bool)>");
      System.exit(1);
    }
    int port = 9099;
    int regPort = 9003;
    String host = args[0];
    String regHost = args[2];
    String message = args[4];
    try {
      port = Integer.parseInt(args[1]);
      regPort = Integer.parseInt(args[3]);
    } catch (NumberFormatException nfe) {
      System.out.println("[Port] must be an integer");
      System.exit(2);
    }

    String target = host + ":" + port;
    ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
            .usePlaintext()
            .build();

    String regTarget = regHost + ":" + regPort;
    ManagedChannel regChannel = ManagedChannelBuilder.forTarget(regTarget)
            .usePlaintext()
            .build();

    try {
      EchoClient client = new EchoClient(channel, regChannel);
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

      System.out.print("Hello welcome to the Service Station!! ");

      boolean exit = false;
      while (!exit) {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Parrot Service");
        System.out.println("2. Joke Service");
        System.out.println("3. Zoo ");
        System.out.println("4. Calculator");
        System.out.println("5. Coffee ");
        System.out.println("6. Restaurant ");
        System.out.println("7. Taxi ");
        System.out.println("8. Exit");

        System.out.print("Enter your choice: ");
        int choice = Integer.parseInt(reader.readLine());

        switch (choice) {
          case 1:
            System.out.print("Enter the message to parrot: ");
            String parrotMessage = reader.readLine();
            client.askServerToParrot(parrotMessage);
            break;
          case 2:
            System.out.print("How many jokes would you like? ");
            int numJokes = Integer.parseInt(reader.readLine());
            client.askForJokes(numJokes);
            break;
          case 3:
            System.out.println("Welcome to the Zoo!");
            client.zooMenu(reader);
            break;
          case 4:
            client.performCalculation(reader);
            break;
          case 5:
            System.out.println("Welcome to the Coffee Menu!");
            client.coffeeMenu(reader);
            break;
          case 6:
            System.out.println("Welcome to the Restaurant Menu!");
            client.restaurantMenu(reader);
            break;
          case 7:
            System.out.println("Welcome to the Taxi Menu!");
            client.taxiMenu(reader);
            break;
          case 8:
            exit = true;
            break;
          default:
            System.out.println("Invalid choice. Please try again.");
        }
      }
    } finally {
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
      regChannel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }
}
