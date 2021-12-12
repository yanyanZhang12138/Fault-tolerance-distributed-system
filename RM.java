import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RM {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: java RM <port number>");
            System.exit(1);
        }
        
        System.out.println("Replication Manager started!");
        System.out.println("RM: 0 members");
        
        int portNumber = Integer.parseInt(args[0]);
        
        ExecutorService executor = null;
        try (ServerSocket serverSocket = new ServerSocket(portNumber);) {
            executor = Executors.newFixedThreadPool(4);
            ArrayList<String> memberShip = new ArrayList<>();
            while (true) {
                Socket rmSocket = serverSocket.accept();
                Runnable worker = new RMRequestHandler(rmSocket, memberShip);
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