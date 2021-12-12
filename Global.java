import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Global {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: java Global <port number>");
            System.exit(1);
        }
        System.out.println("Global Fault Detector started. Listening on Port 8000");
        System.out.println("GPD: 0 members");
        int portNumber = Integer.parseInt(args[0]);
        ExecutorService executor = null;
        try (ServerSocket serverSocket = new ServerSocket(portNumber);) {
            executor = Executors.newFixedThreadPool(4);
            System.out.println("Waiting for Local Fault Detector: ");
            ArrayList<String> memberShip = new ArrayList<>();
            while (true) {
                Socket lfdSocket = serverSocket.accept();
                Runnable worker = new GFDRequestHandler(lfdSocket, memberShip);
                executor.execute(worker);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port" + portNumber + "or listening for a connection");
            System.out.println(e.getMessage());
        } finally {
            if (executor != null) {
                executor.shutdown();
            }
        }
    }
}
