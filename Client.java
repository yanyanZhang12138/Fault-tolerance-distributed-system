import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;

public class Client extends Thread {
    private final Socket client;
    private String clientId;
    private String host;
    private String clientInput;
    private HashSet<String> lookup;
    private String requestNum;


    Client(Socket client, String clientId, String host, String clientInput, HashSet<String> lookup, String requestNum) {
        this.client = client;
        this.clientId = clientId;
        this.host = host;
        this.clientInput = clientInput;
        this.lookup = lookup;
        this.requestNum = requestNum;
    }

    @Override
    public void run() {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out.write(clientId + " : " + clientInput + "Request_num" + requestNum);
            out.newLine();
            out.flush();
            System.out.println("[" + System.currentTimeMillis() + "]" + " Sent " + "<" + clientId + ", " + host + ", " + requestNum + ", " + clientInput + ">");
            String input = in.readLine();
            if (lookup.contains(input)) {
                System.out.println("[" + System.currentTimeMillis() + "]" + "Discarded duplicate reply from " + host);
            } else {
                if (input != null) {
                  System.out.println("[" + System.currentTimeMillis() + "]" + " Received " + "<" + clientId + ", " + host + ", " + requestNum + ", " + input + ">");
                  lookup.add(input);
                }
            }
        } catch (UnknownHostException e) {
            System.out.println("Don't know about host " + host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host);
            try {
                client.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}


