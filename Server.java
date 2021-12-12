import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

// 1324

public class Server {
    private static volatile HashSet<String> myState = new HashSet<>();
    private static volatile int checkPointCount = 0;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);
        String replicaId = args[1];
        String hostNameS2 = args[2];
        String hostNameS3 = args[3];
        int portNumberS2 = Integer.parseInt(args[4]);
        int portNumberS3 = Integer.parseInt(args[5]);
        String mode = args[6];
        String checkPointFrequency = args[7];
        System.out.println("Server started. Listening on Port " + portNumber);
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Waiting");
            if (mode.equals("passive")) {
                Socket serverS2 = new Socket(hostNameS2, portNumberS2);
                Socket serverS3 = new Socket(hostNameS3, portNumberS3);
                Thread serverS2worker = new PassiveServerHandler(serverS2, checkPointFrequency);
                Thread serverS3worker = new PassiveServerHandler(serverS3, checkPointFrequency);
                serverS2worker.start();
                serverS3worker.start();
            }
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread worker = new RequestHandler(clientSocket, replicaId, myState, mode);
                worker.start();

            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port" + portNumber + "or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    private static class PassiveServerHandler extends Thread {
        private final Socket serverClinet;
        String frequency;

        PassiveServerHandler(Socket serverClinet, String checkFrequency) {
            this.serverClinet = serverClinet;
            this.frequency = checkFrequency;
        }

        synchronized void increment() {
            checkPointCount++;
        }

        @Override
        public void run() {
            try {
                PrintWriter out = new PrintWriter(serverClinet.getOutputStream(), true);
                while (true) {
                    StringBuilder builder = new StringBuilder();
                    for (String s : myState) {
                        builder.append(s).append("*");
                    }
                    out.println("primary!" + builder.toString() + "#" + checkPointCount);
                    increment();
                    if (builder.toString().length() == 0) {
                      System.out.println("Sending my_state: " + 0);
                    } else {
                      System.out.println("Sending my_state: " + builder.toString().split("\\*").length);
                    }
                    System.out.println("Sending checkpoint_count: " + checkPointCount);
                    Thread.sleep(Long.parseLong(frequency));
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
