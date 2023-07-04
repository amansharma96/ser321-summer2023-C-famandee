package leader;

import java.util.Scanner;

public class LeaderMain {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: gradle leader --args='<port>'");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        Leader leader = new Leader(port);

        while (true) {
            try {
                leader.start();

                // Accept commands from the leader
                Scanner scanner = new Scanner(System.in);
                boolean running = true;
                while (running) {
                    System.out.print("Enter a command (start, stop): ");
                    String command = scanner.nextLine();

                    switch (command) {
                        case "start":
                            leader.start();
                            break;
                        case "stop":
                            running = false;
                            break;
                        default:
                            System.out.println("Invalid command. Try again.");
                            break;
                    }
                }

                System.out.println("Leader stopped.");
            } catch (Exception e) {
                System.out.println("An exception occurred: " + e.getMessage());
                System.out.println("Restarting Leader...");
            }
        }
    }
}
